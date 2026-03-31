"""
Copyright (c) Facebook, Inc. and its affiliates.

This source code is licensed under the MIT license found in the
LICENSE file in the root directory of this source tree.
"""

from typing import List, Optional
import numpy as np
import torch
import torch.fft
import copy

def fft2c_np(img):

    kspace_cplx = np.fft.fftshift(np.fft.fft2(img))
    kspace = np.zeros((kspace_cplx.shape[0], kspace_cplx.shape[1], 2))
    kspace[:, :, 0] = np.real(kspace_cplx).astype(np.float32)
    kspace[:, :, 1] = np.imag(kspace_cplx).astype(np.float32)

    return kspace

def kspace2img(kspace_,transpose=True):
    #input: [slice,2,256,256]
    if transpose:
        kspace = kspace_.permute((0,2,3,1))
    else:
        kspace = kspace_
    # iimg = torch.zeros((kspace.shape[0],kspace.shape[1],kspace.shape[2]))
    cplximg = torch.zeros((kspace.shape[0],kspace.shape[1],kspace.shape[2],2))
    kspace = kspace.contiguous()
    for i in range(kspace.shape[0]):
        ishift = torch.fft.ifftshift(torch.view_as_complex(kspace[i]))
        cplx = torch.fft.ifft2(ishift)
        # iimg[i,:,:] = torch.abs(cplx)
        cplximg[i,:,:,0] = cplx.real
        cplximg[i,:,:,1] = cplx.imag
    if transpose:
        # iimg = iimg.unsqueeze(1) #[slice,1,256,256]
        cplximg = cplximg.permute((0,3,1,2))
    return cplximg

def img2kspace(img_,transpose=True):
    # input [slice,1,256,256]
    if transpose:
        img = img_.squeeze(1)  # [slice,256,256]
    else:
        img = img_
    ksapce = torch.zeros((img.shape[0],img.shape[1],img.shape[2],2))
    for i in range(img.shape[0]):
        cplx = torch.fft.ifftshift(torch.fft.fft2(img[i]))
        ksapce[i,:,:,0] = torch.real(cplx).to(torch.float32)
        ksapce[i,:,:,1] = torch.imag(cplx).to(torch.float32)
    if transpose:
        ksapce = ksapce.permute((0,3,1,2))
    return ksapce
