import torch
import torch.nn as nn
from .SwinUnet import SwinTransformerUnet
from .PatchGAN import PatchGAN
import yaml
from types import SimpleNamespace
from fftc import *
from utils import *

class Generator(nn.Module):
    def __init__(self,config):
        super(Generator, self).__init__()


        self.unet = SwinTransformerUnet(img_size=256,
                                patch_size=config.patch_size,
                                in_chans=2,
                                num_classes=2,
                                embed_dim=config.embed_dim,
                                depths=config.depths,
                                num_heads=config.num_heads,
                                window_size=config.window_size,
                                mlp_ratio=config.mlp_ratio,
                                qkv_bias=config.qkv_bias,
                                qk_scale=config.qk_scale,
                                drop_rate=config.drop_rate,
                                drop_path_rate=config.drop_path_rate,
                                ape=config.ape,
                                patch_norm=config.patch_norm,
                                use_checkpoint=config.use_checkpoint)



    def forward(self, x):
        x = x+self.unet(x) #im_reconstruc
        return x



class Discriminator(nn.Module):
    def __init__(self,):
        super().__init__()
        self.net = PatchGAN()

    def forward(self, x):
        x = self.net(x)
        return x


if __name__ == '__main__':
    def get_args():
        with open('../config.yaml') as f:
            data = yaml.load(f, Loader=yaml.FullLoader)
        args = SimpleNamespace(**data)

        return args

    args = get_args()
    gnet = Generator(args)
    print(gnet)
    dnet = Discriminator()
    x = torch.randn(30, 2,256,256)

    y,z = gnet(x)
    print(y.min(),y.max())
    # d,clss = dnet(y)
    # print(y.size())
    # print(d.size())
    # print(clss.size())

    print(y.shape)

