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
# code from SwinGAN
def compute_gradient_penalty(device,D, xr, xf):
    # [b,1]
    t = torch.rand(256, 1).to(device)
    # [b,1]=>[b,2]
    t = t.expand_as(xr)
    # 在真实数据和fake数据之间做一个线性插值
    mid = t * xr.detach() + (1 - t) * xf.detach()
    # 设置它需要导数信息
    mid.requires_grad_()

    pred = D(mid)
    grads = torch.autograd.grad(outputs=pred, inputs=mid,
                          grad_outputs=torch.ones_like(pred),
                          create_graph=True, retain_graph=True, only_inputs=True)[0]
    gp = torch.pow(grads.norm(2, dim=1) - 1, 2).mean()

    return gp
####

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


    if load_epoch is not None:
        load_model(gnet,"./save/gnet-{}.pth".format(load_epoch),gnet_optim)
    if load_epoch is None:
        load_epoch=0
    lr_scheduler_gnet = optim.lr_scheduler.StepLR(gnet_optim, step_size=args.gnet_scheduler_step,gamma=args.gnet_scheduler_gamma)

    a,b = evaluate(gnet,device)
    print(a,b)

    dataset = MRNetDataset(args)
    train_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)
    criterionSSIM = SSIM()
    criterionL1 = nn.L1Loss()
    criterionFFL = FFL()
    counter = 0

    for epoch_i in range(args.epochs_n-load_epoch):
        loss_dnet = 0
        loss_gan = 0
        loss_iml1 = 0
        loss_kspacel1 = 0

        for batch_i, dict in enumerate(train_loader):
            counter+=1
            im_gt = dict["im_gt"] #[N,2,256,256]
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

            im_gt = im_gt.to(device)
            undersampled_im = kspace2img(masked_kspace)
            undersampled_im = undersampled_im.to(device)
            kspace_gt = kspace_gt.to(device)



            refined_im = gnet(undersampled_im) #image
            gen_kspace = img2kspace(torch.abs(torch.view_as_complex(refined_im.permute(0,2,3,1))),False)
            gen_kspace = gen_kspace.permute((0,3,1,2))
            gen_kspace = gen_kspace.to(device)




            ##train discriminator
            # real
            # dreal = dnet(im_gt)
            #
            # # fake
            # dfake = dnet(gen_outputs.detach())
            # gradient_penalty = compute_gradient_penalty(device, dnet, im_gt, gen_outputs)
            # loss_d = 1/2*(torch.mean(dfake) - torch.mean(dreal))
            # d_loss = loss_d + 10 * gradient_penalty
            #
            # dnet_optim.zero_grad()
            # d_loss.backward()
            # dnet_optim.step()
            # loss_dnet += loss_d.item()


            # train generator
            # dfake = dnet(gen_outputs)
            # gan_loss = -torch.mean(dfake)
            ksapcel1_loss = criterionFFL(gen_kspace,kspace_gt)
            iml1_loss = criterionL1(refined_im,im_gt)
            g_loss = ksapcel1_loss + iml1_loss




            gnet_optim.zero_grad()
            g_loss.backward()
            gnet_optim.step()
            loss_kspacel1 += ksapcel1_loss.item()
            loss_iml1 += iml1_loss.item()
            # print(d_loss.item(),gan_loss.item(),iml1_loss.item(),imssim_loss.item(),kspacel1_loss.item(),kspacessim_loss.item())
            # if counter%100==0:
            #     print(
            #         "counter:{}\n ksapce_L1_loss:{}\n  im_L1_loss:{}\n  ".format(
            #             counter,ksapcel1_loss.item(), iml1_loss.item(),))
        print(".............................................")
        loss_kspacel1 /= len(train_loader)
        loss_iml1 /= len(train_loader)




        print("Epoch:{}\n  ksapce_L1_loss:{}\n   im_L1_loss:{}\n ".format(epoch_i+load_epoch,loss_kspacel1,  loss_iml1))
        psnr,ssim = evaluate(gnet,device)
        print("Epoch:{}\n psnr:{}\n ssim:{}\n".format(epoch_i+load_epoch,psnr,ssim))
        print(".............................................")
        writer.add_scalar("train/loss iml1", loss_iml1, epoch_i)
        writer.add_scalar("train/loss ksapcel1", loss_kspacel1, epoch_i)
        writer.add_scalar("eval psnr",psnr,epoch_i)
        writer.add_scalar("eval ssim",ssim,epoch_i)
        # learning rate decay
        lr_scheduler_gnet.step()

        # save ckpt
        if epoch_i % 10 == 0:
            save_model(gnet, epoch_i, args.save_path + "gnet",gnet_optim)


def evaluate(gnet,device):
    gnet = gnet.eval()
    dataset = MRNetDataset(args,eval=True)
    train_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)
    psnr_list = []
    ssim_list = []
    with torch.no_grad():
        for batch_i, dict in enumerate(train_loader):
            im_gt = dict["im_gt"] #[N,2,256,256]
            masked_kspace = dict["masked_kspace"]#[N,2,256,256]
            undersampled_im = kspace2img(masked_kspace)
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

    return np.average(np.array(psnr_list)),np.average(np.array(ssim_list))

if __name__ =="__main__":
    args = get_args()
    train(args,load_epoch=40)
