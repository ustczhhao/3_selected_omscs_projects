import torch
import h5py
import numpy as np
import matplotlib.pyplot as plt
from fmri.transforms import *
from fmri.fftc import *
from fmri.mrmath import *
from fmri.subsample import RandomMaskFunc

file_name = './dataset/train/file1000143.h5'
hf = h5py.File(file_name)
print(hf.keys())

volume_kspace = hf['kspace'][()] #(number of slices, height, width)
slice_kspace = volume_kspace[20] # Choosing the 20-th slice of this volume [640,368]
slice_kspace2 = to_tensor(slice_kspace)#[640,368,2]  # Convert to PyTorch tensor
print(slice_kspace2.shape)
volume_gt = hf['reconstruction_esc'][()] #(number of slices, height, width)
slice_gt = volume_gt[20] # Choosing the 20-th slice of this volume


slice_image = ifft2c(slice_kspace2)     #[640,368,2]      # Apply Inverse Fourier Transform to get the complex image
slice_image_abs = complex_abs(slice_image) #[640,368]   # Compute absolute value to get a real image

mask_func = RandomMaskFunc(center_fractions=[0.04], accelerations=[8])  # Create the mask function object

masked_kspace, mask, _ = apply_mask(slice_kspace2, mask_func)#[640,368,2]

sampled_image = ifft2c(masked_kspace)           # Apply Inverse Fourier Transform to get the complex image
sampled_image_abs = complex_abs(sampled_image)   # Compute absolute value to get a real image
#
# plt.figure()
# plt.imshow(np.log(np.abs(slice_kspace) + 1e-9),cmap="gray")
# plt.figure()
# plt.imshow(slice_image_abs,cmap="gray")
# plt.figure()
# plt.imshow(slice_gt,cmap="gray")
# plt.figure()
# plt.imshow(np.log(np.abs(tensor_to_complex_np(masked_kspace)) + 1e-9),cmap="gray")
# plt.figure()
# plt.imshow(sampled_image_abs, cmap='gray')
# plt.show()