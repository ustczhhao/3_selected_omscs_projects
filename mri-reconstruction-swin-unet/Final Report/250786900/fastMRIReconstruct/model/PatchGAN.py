""" Full assembly of the parts to form the complete network """
import torch
import torch.nn.functional as F
import scipy
from torch import nn
import functools


# code from PatchGAN, https://github.com/He-jerry/PatchGAN
class PatchGAN(nn.Module):
    def weights_init(self,m):
        # print(m.__class__.__name__)
        if isinstance(m, nn.Conv2d):
            nn.init.kaiming_normal_(m.weight, mode="fan_out", nonlinearity="leaky_relu")
            if m.bias is not None:
                nn.init.constant_(m.bias, 0)
        elif isinstance(m, nn.ConvTranspose2d):
            nn.init.kaiming_normal_(m.weight, mode="fan_out", nonlinearity="leaky_relu")
            if m.bias is not None:
                nn.init.constant_(m.bias, 0)
        elif isinstance(m, nn.BatchNorm2d):
            nn.init.constant_(m.weight, 1)
            nn.init.constant_(m.bias, 0)
        elif isinstance(m, nn.Linear):
            nn.init.normal_(m.weight, 0, 0.01)
            nn.init.constant_(m.bias, 0)

    def __init__(self, input_nc=2, ndf=64, n_layers=4,FC_bottleneck=False):
        """Construct a PatchGAN discriminator  - #https://github.com/junyanz/pytorch-CycleGAN-and-pix2pix
        Modified to have double-convs, cropping, and a bottle-neck to use a vanilla dicriminator
        Parameters:
            input_nc (int)  -- the number of channels in input images
            ndf (int)       -- the number of filters in the last conv layer
            n_layers (int)  -- the number of conv layers in the discriminator
            norm_layer      -- normalization layer
            crop_center      -- None ot the size of the center patch to be cropped
            FC_bottleneck      -- If True use global average pooling and output a one-dimension prediction
        """
        super(PatchGAN, self).__init__()

        use_bias = False
        kw = 3
        padw = 1
        sequence = [nn.Conv2d(input_nc, ndf, kernel_size=kw, stride=1, padding=padw),
                    nn.LeakyReLU(0.2, True),
                    nn.Conv2d(ndf, ndf, kernel_size=kw, stride=2, padding=padw),
                    nn.LeakyReLU(0.2, True)]
        nf_mult = 1
        nf_mult_prev = 1
        for n in range(1, n_layers):  # gradually increase the number of filters
            nf_mult_prev = nf_mult
            nf_mult = min(2 ** n, 8)
            sequence += [
                nn.Conv2d(ndf * nf_mult_prev, ndf * nf_mult, kernel_size=kw, stride=1, padding=padw, bias=use_bias),
                nn.BatchNorm2d(ndf*nf_mult),
                nn.LeakyReLU(0.2, True),
                nn.Conv2d(ndf * nf_mult, ndf * nf_mult, kernel_size=kw, stride=2, padding=padw, bias=use_bias),
                nn.BatchNorm2d(ndf * nf_mult),
                nn.LeakyReLU(0.2, True),
            ]

        nf_mult_prev = nf_mult
        nf_mult = min(2 ** n_layers, 8)
        sequence += [nn.Conv2d(ndf*nf_mult_prev,ndf*nf_mult,kernel_size=kw,stride=1,padding=padw,bias=use_bias),
                     nn.BatchNorm2d(ndf * nf_mult),
                     nn.LeakyReLU(0.2,True)
        ]

        if FC_bottleneck:
            sequence += [nn.AdaptiveAvgPool2d((1, 1)),
                         nn.Flatten(), nn.Linear(ndf * nf_mult, 128),
                         nn.LeakyReLU(0.2, True),
                         nn.Linear(128, 1)]
        else:
            sequence += [
                nn.Conv2d(ndf * nf_mult, 1, kernel_size=kw, stride=1, padding=padw)]  # output 1 channel prediction map
        self.model = nn.Sequential(*sequence).apply(self.weights_init)


    def forward(self, input):
        x = self.model(input)


        return x
####

if __name__ == "__main__":
    model = PatchGAN()
    print(model)
    x = torch.randn(16, 2, 256, 256)
    a = model(x)
    print(a.shape)