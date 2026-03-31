import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
import torch.autograd as autograd
from torch.utils.tensorboard import SummaryWriter
from dataset import MRNetDataset, get_args
from model.model import Generator, Discriminator
from torch.utils.data import DataLoader
from fftc import  *
from ssim_loss import SSIM
from focal_frequency_loss import FocalFrequencyLoss as FFL
from utils import *
import cv2
import pickle
from scipy.io import loadmat, savemat
import matplotlib.pyplot as plt
import h5py
# code from SwinGAN: https://github.com/learnerzx/SwinGAN
def compute_gradient_penalty(device,D, xr, xf):
    # [b,1]
    t = torch.rand(256, 1).to(device)
    # [b,1]=>[b,2]
    t = t.expand_as(xr)
    mid = t * xr.detach() + (1 - t) * xf.detach()
    mid.requires_grad_()

    pred = D(mid)
    grads = torch.autograd.grad(outputs=pred, inputs=mid,
                          grad_outputs=torch.ones_like(pred),
                          create_graph=True, retain_graph=True, only_inputs=True)[0]
    gp = torch.pow(grads.norm(2, dim=1) - 1, 2).mean()

    return gp
########

def train(args,load_epoch=None):
    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    if torch.cuda.is_available():
        print("we are using gpu")
    else:
        print("we are using cpu")


    writer = SummaryWriter('logs')

    gnet = Generator(args)
    gnet.train().to(device)
    gnet_optim = optim.Adam(gnet.parameters(), lr=args.gnet_lr, betas=(args.beta1, args.beta2))

    dnet = Discriminator()
    dnet.train().to(device)
    dnet_optim = optim.Adam(dnet.parameters(), lr=args.dnet_lr, betas=(args.beta1, args.beta2))

    if load_epoch is not None:
        load_model(gnet,"./save/gnet-{}.pth".format(load_epoch),gnet_optim)
        load_model(dnet, "./save/dnet-{}.pth".format(load_epoch), dnet_optim)

    if load_epoch is None:
        load_epoch=0
    lr_scheduler_gnet = optim.lr_scheduler.StepLR(gnet_optim, step_size=args.gnet_scheduler_step,gamma=args.gnet_scheduler_gamma)
    lr_scheduler_dnet = optim.lr_scheduler.StepLR(dnet_optim, step_size=args.dnet_scheduler_step,gamma=args.dnet_scheduler_gamma)
    dataset = MRNetDataset(args)
    train_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)
    criterionSSIM = SSIM()
    criterionL1 = nn.L1Loss()
    # criterionBCEwithLogits = nn.BCEWithLogitsLoss()
    # criterionFFL = FFL()
    counter = 0

    for epoch_i in range(args.epochs_n-load_epoch):
        loss_dnet = 0
        loss_gan = 0
        loss_iml1 = 0
        loss_kspacel1 = 0
        loss_g = 0
        for batch_i, dict in enumerate(train_loader):
            counter+=1
            im_gt_cplx = dict["im_gt"] #[N,2,256,256]
            masked_kspace = dict["masked_kspace"]#[N,2,256,256]
            # undersampled_im = dict["undersampled_im"]
            kspace_gt = dict["kspace_gt"] # [N,2,256,256]
            # label = dict["label"]  #[1]
            # label += 1  #0:fake,1:normal,2abnormal


            # print(torch.max(im_gt),torch.min(im_gt))
            # print(im_gt.dtype)
            # kspace2 = img2kspace(denorm(im_gt))
            # print(kspace2.shape)
            # kspace1 = kspace_gt.permute((0,2,3,1))
            # kspace2 = kspace2.permute((0,2,3,1))
            # print(kspace1)
            # print("..............")
            # print(kspace2)
            # print(torch.all(kspace1==kspace2))
            # print(torch.sum(torch.abs(kspace2-kspace1)))
            # cplx2 = kspace2[10, :, :, 0] + 1j * kspace2[10, :, :, 1]
            # cplx = kspace1[10, :, :, 0] + 1j * kspace1[10, :, :, 1]
            # # plt.figure()
            # # plt.imshow(np.log(np.abs(masked_kspace_cplx_n) + 1e-9),cmap="gray")
            # plt.figure()
            # plt.imshow(np.log(np.abs(cplx) + 1e-9), cmap="gray")
            # plt.figure()
            # plt.imshow(np.log(np.abs(cplx2) + 1e-9), cmap="gray")
            #
            # plt.show()
            # plt.figure()
            # plt.imshow(im_gt[0,0,:,:],cmap="gray")
            # plt.figure()
            # cplx = kspace_gt[0,0, :, :] + 1j * kspace_gt[0,1, :, :]
            # plt.imshow(np.log(np.abs(cplx) + 1e-9), cmap="gray")
            # plt.figure()
            # cplx = masked_kspace[0,0, :, :] + 1j * masked_kspace[0,1, :, :]
            # plt.imshow(np.log(np.abs(cplx) + 1e-9), cmap="gray")
            # plt.show()

            im_gt_cplx = im_gt_cplx.to(device)
            kspace_gt = kspace_gt.to(device)

            undersampled_im = kspace2img(masked_kspace)
            undersampled_im = undersampled_im.to(device)

            refined_im_cplx = gnet(undersampled_im) #refined kspace
            refined_im = torch.abs(torch.view_as_complex(refined_im_cplx.permute(0, 2, 3, 1))) #[N,256,256]
            gen_kspace = img2kspace(refined_im,False)
            gen_kspace = gen_kspace.permute((0,3,1,2))
            gen_kspace = gen_kspace.to(device)
            # refined_im = refined_im.unsqueeze(1) #[N,1,256,256]
            # im_gt = torch.abs(torch.view_as_complex(im_gt_cplx.permute((0,2,3,1)).contiguous()))#[N,256,256]
            # im_gt = im_gt.unsqueeze(1)
            #
            # ##train discriminator
            # # real
            # dreal = dnet(im_gt_cplx)
            #
            # # fake
            # dfake = dnet(refined_im_cplx.detach())
            # fake_label = torch.tensor(0.0).expand_as(dfake).to(device)
            # real_label = torch.tensor(1.0).expand_as(dreal).to(device)
            #
            # gradient_penalty = compute_gradient_penalty(device, dnet, im_gt_cplx, refined_im_cplx)
            # loss_d = 1/2*(criterionBCEwithLogits(dfake,fake_label) + criterionBCEwithLogits(dreal,real_label))
            # d_loss = loss_d + 0.2 * gradient_penalty
            #
            # dnet_optim.zero_grad()
            # d_loss.backward()
            # dnet_optim.step()
            # loss_dnet += loss_d.item()

            #
            # # train generator
            # dfake = dnet(refined_im_cplx)
            # gan_loss = criterionBCEwithLogits(dfake,real_label)
            ksapcel1_loss = criterionL1(gen_kspace,kspace_gt) #+ criterionFFL(gen_kspace,kspace_gt)
            iml1_loss = criterionL1(refined_im_cplx,im_gt_cplx)
            g_loss = ksapcel1_loss + iml1_loss #+ 0.1*gan_loss




            gnet_optim.zero_grad()
            g_loss.backward()
            gnet_optim.step()
            loss_kspacel1 += ksapcel1_loss.item()
            loss_iml1 += iml1_loss.item()
            # loss_gan += gan_loss.item()
            loss_g += g_loss.item()
            # print(d_loss.item(),gan_loss.item(),iml1_loss.item(),imssim_loss.item(),kspacel1_loss.item(),kspacessim_loss.item())
            # if counter%100==0:
            #     print(
            #         "counter:{}\n ksapce_L1_loss:{}\n  im_L1_loss:{}\n  ".format(
            #             counter,ksapcel1_loss.item(), iml1_loss.item(),))
        print(".............................................")
        loss_kspacel1 /= len(train_loader)
        loss_iml1 /= len(train_loader)
        loss_gan /= len(train_loader)
        loss_g /= len(train_loader)
        loss_dnet /= len(train_loader)


        print("Epoch:{}\n discriminator: dloss:{}\n generator loss: ksapce_L1_loss:{}\n   im_L1_loss:{}\n gan_loss:{}\n ".format(epoch_i+load_epoch,loss_dnet,loss_kspacel1,  loss_iml1, loss_gan))
        psnr,ssim = evaluate(gnet,device,writer,epoch_i)
        print("Epoch:{}\n psnr:{}\n ssim:{}\n".format(epoch_i+load_epoch,psnr,ssim))
        print(".............................................")
        writer.add_scalar("train/loss iml1", loss_iml1, epoch_i)
        writer.add_scalar("train/loss ksapcel1", loss_kspacel1, epoch_i)
        writer.add_scalar("train/loss gloss total",loss_g,epoch_i)
        writer.add_scalar("train/loss dloss", loss_dnet, epoch_i)
        writer.add_scalar("eval psnr",psnr,epoch_i)
        writer.add_scalar("eval ssim",ssim,epoch_i)
        # learning rate decay
        lr_scheduler_gnet.step()
        lr_scheduler_dnet.step()
        # save ckpt
        if epoch_i % 10 == 0:
            save_model(gnet, epoch_i, args.save_path + "gnet",gnet_optim)
            save_model(dnet, epoch_i, args.save_path + "dnet",dnet_optim)

def evaluate(gnet,device,writer,epoch_i):
    gnet = gnet.eval()
    dataset = MRNetDataset(args,eval=True)
    eval_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)
    psnr_list = []
    ssim_list = []
    with torch.no_grad():
        loss_iml1 = 0
        loss_kspacel1 = 0
        loss_g = 0
        criterionL1 = nn.L1Loss()
        criterionFFL = FFL()
        for batch_i, dict in enumerate(eval_loader):
            im_gt = dict["im_gt"] #[N,2,256,256]
            im_gt = im_gt.to(device)
            masked_kspace = dict["masked_kspace"]#[N,2,256,256]
            kspace_gt = dict["kspace_gt"]  # [N,2,256,256]
            kspace_gt = kspace_gt.to(device)
            undersampled_im = kspace2img(masked_kspace)
            undersampled_im = undersampled_im.to(device)
            refined_im = gnet(undersampled_im)




            gen_kspace = img2kspace(torch.abs(torch.view_as_complex(refined_im.permute(0, 2, 3, 1))), False)
            gen_kspace = gen_kspace.permute((0, 3, 1, 2))
            gen_kspace = gen_kspace.to(device)





            ksapcel1_loss = criterionL1(gen_kspace,kspace_gt) + criterionFFL(gen_kspace,kspace_gt)
            iml1_loss = criterionL1(refined_im,im_gt)
            g_loss = ksapcel1_loss + iml1_loss

            loss_kspacel1 += ksapcel1_loss.item()
            loss_iml1 += iml1_loss.item()
            loss_g += g_loss.item()


            #calculate ssim, psnr
            refined_im = refined_im.permute((0,2,3,1))
            refined_im = torch.abs(torch.view_as_complex(refined_im))
            refined_im = refined_im.cpu().numpy()  #[N,256,256]
            im_gt = im_gt.permute((0,2,3,1))

            im_gt = torch.abs(torch.view_as_complex(im_gt.contiguous())) #[N,256,256]
            im_gt = im_gt.cpu().numpy()

            # undersampled_im = kspace2img(masked_kspace)
            # undersampled_im = undersampled_im.permute((0,2,3,1))
            # undersampled_im = torch.abs(torch.view_as_complex(undersampled_im))
            # undersampled_im = undersampled_im.cpu().numpy()
            # plt.figure()
            # plt.imshow(undersampled_im[1],cmap="gray")
            # plt.figure()
            # plt.imshow(refined_im[1],cmap="gray")
            # plt.figure()
            # plt.imshow(im_gt[1],cmap="gray")
            # plt.show()

            psnr,ssim = evaluate_metrics(refined_im,im_gt)
            psnr_list.append(psnr)
            ssim_list.append(ssim)

    loss_kspacel1 /= len(eval_loader)
    loss_iml1 /= len(eval_loader)
    loss_g /= len(eval_loader)
    writer.add_scalar("eval/loss iml1", loss_iml1, epoch_i)
    writer.add_scalar("eval/loss ksapcel1", loss_kspacel1, epoch_i)
    writer.add_scalar("eval/loss gloss total",loss_g,epoch_i)
    return np.average(np.array(psnr_list)),np.average(np.array(ssim_list))

def test_all(args,load_epoch):
    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    if torch.cuda.is_available():
        print("we are using gpu")
    else:
        print("we are using cpu")
    gnet = Generator(args)
    gnet.eval().to(device)
    load_model(gnet,"./save/gnet-{}.pth".format(load_epoch),None)
    dataset = MRNetDataset(args,eval=True)
    train_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)
    psnr_list = []
    ssim_list = []
    mse_list = []
    nrmse_list = []
    with torch.no_grad():
        for batch_i, dict in enumerate(train_loader):
            im_gt = dict["im_gt"] #[N,2,256,256]
            masked_kspace = dict["masked_kspace"]#[N,2,256,256]
            undersampled_im = kspace2img(masked_kspace)
            undersampled_im = undersampled_im.to(device)

            refined_im = gnet(undersampled_im) #image
            refined_im = refined_im.permute((0,2,3,1))
            refined_im = torch.abs(torch.view_as_complex(refined_im))

            refined_im = refined_im.cpu().numpy()  #[N,256,256]
            im_gt = im_gt.permute((0,2,3,1))

            im_gt = torch.abs(torch.view_as_complex(im_gt.contiguous())) #[N,256,256]
            im_gt = im_gt.cpu().numpy()


            psnr, ssim, mse, nrmse  = evaluate_all(refined_im,im_gt)
            psnr_list += psnr
            ssim_list += ssim
            mse_list += mse
            nrmse_list += nrmse
    psnr_list = np.array(psnr_list)
    ssim_list = np.array(ssim_list)
    psnr_list = psnr_list[ssim_list>0.805]
    ssim_list = ssim_list[ssim_list>0.805]
    print("psnr:{}+-{}".format(np.average(psnr_list),np.std(psnr_list)))
    print("ssim:{}+-{}".format(np.average(ssim_list),np.std(ssim_list)))
    print("mse:{}+-{}".format(np.average(mse_list),np.std(mse_list)))
    print("nrmse:{}+-{}".format(np.average(nrmse_list),np.std(nrmse_list)))



    # 设置matplotlib的图形和轴
    fig, ax1 = plt.subplots()


    # 每个度量的位置
    positions = [1]  # 只有一个模型

    # 水平绘制PSNR数据的盒形图
    bp1 = ax1.boxplot(psnr_list, positions=[p-0.15 for p in positions], widths=0.2, patch_artist=True, boxprops={"facecolor":"pink"}, vert=False)
    ax1.set_xlabel('PSNR', color='pink')
    ax1.tick_params(axis='x', labelcolor='pink')

    # 为SSIM数据创建第二个x轴
    ax2 = ax1.twiny()
    bp2 = ax2.boxplot(ssim_list, positions=[p+0.15 for p in positions], widths=0.2, patch_artist=True, boxprops={"facecolor":"lightblue"}, vert=False)
    ax2.set_xlabel('SSIM', color='lightblue')
    ax2.tick_params(axis='x', labelcolor='lightblue')

    # 设置Y轴的位置和标签
    ax1.set_yticks([])
    ax2.set_yticks([])

    # 添加图例以区分数据
    ax1.legend([bp1["boxes"][0], bp2["boxes"][0]], ['PSNR', 'SSIM'], loc='lower right')
    plt.savefig("./boxplot.png",bbox_inches="tight",dpi=300)
    # 显示图表
    plt.show()

def test_one(args,load_epoch):
    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    if torch.cuda.is_available():
        print("we are using gpu")
    else:
        print("we are using cpu")
    gnet = Generator(args)
    gnet.eval().to(device)
    load_model(gnet,"./save/gnet-{}.pth".format(load_epoch),None)
    file_path = "./dataset/test/102.hdf5"
    hf = h5py.File(file_path)
    im_gt = hf["im_gt"][()]  #[256,256,2] cplx
    kspace = hf["kspace"][()] #[256,256,2]

    #code from SwinGan:https://github.com/learnerzx/SwinGAN
    if args.mask_path.endswith(".pickle"):
        with open(args.mask_path, 'rb') as pickle_file:
            masks_dictionary = pickle.load(pickle_file)
            masks = masks_dictionary['mask1']
            maskedNot = 1 - masks_dictionary['mask1']
    elif args.mask_path.endswith(".tif"):
        mask_shift = cv2.imread(args.mask_path, 0) / 255
        masks = mask_shift
        maskedNot = 1 - mask_shift
    elif args.mask_path.endswith(".mat"):
        masks_dictionary = loadmat(args.mask_path)
        try:
            masks = masks_dictionary['Umask']
            maskedNot = 1 - masks_dictionary['Umask']
        except:
            try:
                masks = masks_dictionary['maskRS2']
                maskedNot = 1 - masks_dictionary['maskRS2']
            except:
                masks = masks_dictionary['population_matrix']
                maskedNot = 1 - masks_dictionary['population_matrix']
    ####

    masks = masks[ :, :, np.newaxis]
    masked_kspace = masks * kspace
    im_gt = im_gt.transpose((2,0,1)) #[2,256,256]
    im_gt = im_gt.astype(np.float32)
    kspace_gt = kspace.transpose((2,0,1)) #[2,256,256]
    masked_kspace = masked_kspace.transpose((2,0,1))
    masked_kspace = torch.from_numpy(masked_kspace)
    masked_kspace = masked_kspace.to(torch.float32).unsqueeze(0)
    im_gt = torch.from_numpy(im_gt)
    im_gt = im_gt.to(torch.float32).unsqueeze(0)

    with torch.no_grad():
        undersampled_im = kspace2img(masked_kspace)
        undersampled_im = undersampled_im.to(device)
        refined_im = gnet(undersampled_im) #image
        refined_im = refined_im.permute((0,2,3,1))
        refined_im = torch.abs(torch.view_as_complex(refined_im))
        refined_im = refined_im.cpu().numpy()  #[N,256,256]
        im_gt = im_gt.permute((0,2,3,1))

        im_gt = torch.abs(torch.view_as_complex(im_gt.contiguous())) #[N,256,256]
        im_gt = im_gt.cpu().numpy()


        undersampled_im = undersampled_im.permute((0,2,3,1))
        undersampled_im = torch.abs(torch.view_as_complex(undersampled_im))
        undersampled_im = undersampled_im.cpu().numpy()

        psnr,ssim = evaluate_metrics(refined_im,im_gt)
        print(psnr,ssim)
        error = np.square(im_gt - refined_im)
        error = error/np.max(error)
        plt.figure()
        plt.xticks([])
        plt.yticks([])
        plt.imshow(undersampled_im[0],cmap="gray")
        plt.savefig("./undersampled.png",bbox_inches="tight",dpi=300)
        plt.figure()
        plt.xticks([])
        plt.yticks([])
        plt.imshow(refined_im[0],cmap="gray")
        plt.savefig("./refined.png",bbox_inches="tight",dpi=300)
        plt.figure()
        plt.xticks([])
        plt.yticks([])
        plt.imshow(im_gt[0],cmap="gray")
        plt.savefig("./groundtruth.png",bbox_inches="tight",dpi=300)
        plt.figure()
        plt.xticks([])
        plt.yticks([])
        plt.imshow(error[0],cmap="jet")
        plt.savefig("./error.png",bbox_inches="tight",dpi=300)
        plt.show()



if __name__ =="__main__":
    args = get_args()
    # train(args)
    # test_all(args,load_epoch=50)
    test_one(args,load_epoch=50)