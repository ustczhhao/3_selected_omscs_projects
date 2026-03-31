import torch
import numpy as np
import matplotlib.pyplot as plt

def kspace2img(kspace):
    ishift = torch.fft.ifftshift(torch.view_as_complex(kspace))
    iimg = torch.fft.ifft2(ishift)
    iimg = torch.abs(iimg)
    return iimg

def fft2(img):
    return np.fft.fftshift(np.fft.fft2(img))

im = np.load("./mrnet/train/0010.npy") #slice,256,256 image,full sampled
# im_slice = im[20] #256,256
kspace_cplx = fft2(im)
kspace = np.zeros((kspace_cplx.shape[0],256, 256, 2))
kspace[:,:, :, 0] = np.real(kspace_cplx).astype(np.float32)
kspace[:,:, :, 1] = np.imag(kspace_cplx).astype(np.float32)
print(np.min(kspace),np.max(kspace))
# print(kspace_slice.shape)

f = kspace[:,:, :, 0]+ 1j*kspace[:,:, :, 1] #complex
ishift = np.fft.ifftshift(f)
iimg = np.fft.ifft2(ishift)
iimg = np.abs(iimg)
print(iimg.shape,iimg.dtype)

gt = kspace2img(torch.from_numpy(kspace))

plt.figure()
# plt.imshow(np.log(np.abs(kspace_cplx) + 1e-9),cmap="gray")
plt.figure()
plt.imshow(im[10],cmap="gray")
plt.figure()
plt.imshow(gt[10],cmap="gray")
plt.show()