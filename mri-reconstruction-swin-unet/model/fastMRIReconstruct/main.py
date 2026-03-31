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
from utils import *
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

    dnet = Discriminator()
    dnet.train().to(device)
    dnet_optim = optim.Adam(dnet.parameters(), lr=args.dnet_lr, betas=(args.beta1, args.beta2))


    if load_epoch is not None:
        load_model(dnet,"./save/dnet-{}.pth".format(load_epoch),dnet_optim)
        load_model(gnet,"./save/gnet-{}pth".format(load_epoch),gnet_optim)

    lr_scheduler_gnet = optim.lr_scheduler.StepLR(gnet_optim, step_size=args.gnet_scheduler_step,gamma=args.gnet_scheduler_gamma)
    lr_scheduler_dnet = optim.lr_scheduler.StepLR(dnet_optim, step_size=args.dnet_scheduler_step,gamma=args.dnet_scheduler_gamma)


    dataset = MRNetDataset(args)
    train_loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True, num_workers=3)

    criterioL1 = nn.SmoothL1Loss()
    criterioSSIM = SSIM()
    counter = 0
    for epoch_i in range(args.epochs_n):
        loss_dnet = 0
        loss_gan = 0
        loss_iml1 = 0
        loss_imssim = 0
        loss_ksapcel1 = 0
        loss_ksapcessim = 0

        for batch_i, dict in enumerate(train_loader):
            counter+=1
            im_gt = dict["im_gt"] #[N,1,256,256]
            kspace_gt = dict["kspace_gt"] #[N,2,256,256]
            masked_kspace = dict["masked_kspace"]#[N,2,256,256]
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




            im_gt = im_gt.to(device)
            kspace_gt = kspace_gt.to(device)
            masked_kspace = masked_kspace.to(device)


            gen_outputs = gnet(masked_kspace) #image
            ##train discriminator
            # real
            dreal = dnet(im_gt)

            # fake
            dfake = dnet(gen_outputs.detach())
            gradient_penalty = compute_gradient_penalty(device, dnet, im_gt, gen_outputs)
            loss_d = 1/2*(torch.mean(dfake) - torch.mean(dreal))
            d_loss = loss_d + 10 * gradient_penalty

            dnet_optim.zero_grad()
            d_loss.backward()
            dnet_optim.step()
            loss_dnet += loss_d.item()


            # train generator
            dfake = dnet(gen_outputs)
            gan_loss = -torch.mean(dfake)
            iml1_loss = criterioL1(gen_outputs,im_gt)
            imssim_loss = 1-criterioSSIM(gen_outputs,im_gt)

            g_loss = gan_loss + iml1_loss + imssim_loss
            gnet_optim.zero_grad()
            g_loss.backward()
            gnet_optim.step()
            loss_gan += gan_loss.item()
            loss_iml1 += iml1_loss.item()
            loss_imssim += imssim_loss.item()

            # print(d_loss.item(),gan_loss.item(),iml1_loss.item(),imssim_loss.item(),kspacel1_loss.item(),kspacessim_loss.item())
            if counter%100==0:
                print(
                    "counter:{}\n   Generator_g_loss:{}\n im_L1_loss:{}\n im_SSIM_loss:{}\n  Discriminator_loss:{}".format(
                        counter,gan_loss.item(), iml1_loss.item(), imssim_loss.item(),  loss_d.item()))
        print(".............................................")
        loss_dnet /= len(train_loader)
        loss_gan /= len(train_loader)
        loss_iml1 /= len(train_loader)
        loss_imssim /= len(train_loader)
        loss_ksapcel1 /= len(train_loader)
        loss_ksapcessim /= len(train_loader)


        writer.add_scalar("train/loss dnet loss", loss_dnet, epoch_i)
        writer.add_scalar("train/loss gnet loss", loss_gan+loss_iml1+loss_imssim, epoch_i)
        writer.add_scalar("train/loss gan loss", loss_gan, epoch_i)
        writer.add_scalar("train/loss iml1", loss_iml1, epoch_i)
        writer.add_scalar("train/loss imssim", loss_imssim, epoch_i)

        print("Epoch:{}\n   Generator_g_loss:{}\n im_L1_loss:{}\n im_SSIM_loss:{}\n  Discriminator_loss:{}".format(
        epoch_i, loss_gan, loss_iml1, loss_imssim, loss_dnet))
        print(".............................................")
        # learning rate decay
        lr_scheduler_dnet.step()
        lr_scheduler_gnet.step()

        # save ckpt
        if epoch_i % 10 == 0:
            save_model(gnet, epoch_i, args.save_path + "gnet",gnet_optim)
            save_model(dnet, epoch_i, args.save_path + "dnet",dnet_optim,)

if __name__ =="__main__":
    args = get_args()
    train(args)
