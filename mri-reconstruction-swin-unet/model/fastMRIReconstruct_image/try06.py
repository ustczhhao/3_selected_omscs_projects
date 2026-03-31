
import h5py
import matplotlib.pyplot as plt
import numpy as np
import torch
import yaml
from types import SimpleNamespace
import cv2
import pickle
from scipy.io import loadmat, savemat
from fftc import  *

def img2kspace(img):
    ksapce = torch.zeros((img.shape[0],img.shape[1],img.shape[2],2))
    for i in range(img.shape[0]):
        cplx = torch.fft.ifftshift(torch.fft.fft2(img[i]))
        ksapce[i,:,:,0] = torch.real(cplx).to(torch.float32)
        ksapce[i,:,:,1] = torch.imag(cplx).to(torch.float32)
    return ksapce


def kspace2img(kspace):
    iimg = torch.zeros((kspace.shape[0],kspace.shape[1],kspace.shape[2]))
    for i in range(kspace.shape[0]):
        ishift = torch.fft.ifftshift(torch.view_as_complex(kspace[i]))
        cplx = torch.fft.ifft2(ishift)
        iimg[i,:,:] = torch.abs(cplx)
    return iimg
def complex_abs(data: torch.Tensor) -> torch.Tensor:
    """
    Compute the absolute value of a complex valued input tensor.

    Args:
        data: A complex valued tensor, where the size of the final dimension
            should be 2.

    Returns:
        Absolute value of data.
    """
    if not data.shape[-1] == 2:
        raise ValueError("Tensor does not have separate complex dim.")

    return (data**2).sum(dim=-1).sqrt()
def get_args():
    with open('./config.yaml') as f:
        data = yaml.load(f, Loader=yaml.FullLoader)
    args = SimpleNamespace(**data)
    if args.mask_type == 'random':
        args.mask_path = './Masks/random/mask_{}_{}.pickle'.format(args.sampling_percentage, args.img_size)
    else:
        args.mask_path = './Masks/{}/{}_{}_{}_{}.mat'.format(args.mask_type, args.mask_type, args.img_size,
                                                             args.img_size, args.sampling_percentage)
    return args
args = get_args()
def fft2(img):
    return np.fft.fftshift(np.fft.fft2(img,axes=(-1,-2)))

hf = h5py.File(args.train_dataset_path+"0014.hdf5")
label = hf["label"][()]
label = np.array([label])

im = hf["im_gt"][()]
kspace = hf["kspace"][()]
print(np.min(kspace),np.max(kspace))

# mask_path = args.mask_path
# if args.mask_type == 'radial' and args.sampling_percentage == 30:
#     with open(mask_path, 'rb') as pickle_file:
#         masks_dictionary = pickle.load(pickle_file)
#         # mask = torch.tensor(masks['mask1'] == 1, device=args.device)
#         masks = masks_dictionary
#         maskedNot = 1 - masks_dictionary
# elif args.mask_type == 'radial' and args.sampling_percentage == 50:
#     mask_shift = cv2.imread(r'E:\code\code_backup\Masks\radial\radial_50.tif', 0) / 255
#     # mask = scipy.ifft(mask_shift)
#     # mask = torch.tensor(mask_shift == 1, device=args.device)
#     masks = mask_shift
#     maskedNot = 1 - mask_shift
#
# elif args.mask_type == 'random':
#     with open(mask_path, 'rb') as pickle_file:
#         masks_dictionary = pickle.load(pickle_file)
#         masks = masks_dictionary['mask1']
#         maskedNot = 1 - masks_dictionary['mask1']
# else:
#     masks_dictionary = loadmat(mask_path)
#     try:
#         masks = masks_dictionary['Umask']
#         maskedNot = 1 - masks_dictionary['Umask']
#     except:
#         try:
#             masks = masks_dictionary['maskRS2']
#             maskedNot = 1 - masks_dictionary['maskRS2']
#         except:
#             masks = masks_dictionary['population_matrix']
#             maskedNot = 1 - masks_dictionary['population_matrix']
#
#
# masks = masks[np.newaxis,:,:,np.newaxis]
# masked_kspace = masks*kspace
#
#
#
#
# masked_kspace_n = masked_kspace[20]
# masked_kspace_cplx_n = masked_kspace_n[:,:,0]+1j*masked_kspace_n[:,:,1]
#
# plt.figure()
# plt.imshow(np.log(np.abs(masked_kspace_cplx_n) + 1e-9),cmap="gray")
# print(kspace.shape)
#
# print(masked_kspace_n.shape)
# # unsampled_im = ifft2c(torch.from_numpy(kspace[20]))
# # slice_image_abs = complex_abs(unsampled_im)

# gt = kspace2img(torch.from_numpy(kspace))
# # unsampled_im = fftshift(inverseFT(torch.from_numpy(masked_kspace))).squeeze(1).numpy()
# print(gt.shape)
# print(im.shape)
# # unsampled_slice_clpx = unsampled_im[20].numpy()
# # unsampled_slice = np.sqrt((unsampled_slice_clpx.real**2+unsampled_slice_clpx.imag**2))
# # print(unsampled_slice.shape)
# # plt.figure()
# # plt.imshow(gt[20],cmap="gray")
# criterion = torch.nn.L1Loss()
# gt.requires_grad = True
# loss = criterion(gt,torch.from_numpy(im))
# print(loss)
# loss.backward()
#
# plt.figure()
# plt.imshow(gt[10],cmap="gray")
# plt.figure()
plt.imshow(im[1],cmap="gray")
# plt.figure()
# # plt.imshow(unsampled_slice, cmap='gray')
# plt.show()


# kspace2 = img2kspace(torch.from_numpy(im))
#
# cplx2 = kspace2[10,:,:,0]+1j*kspace2[10,:,:,1]
# cplx = kspace[10,:,:,0]+1j*kspace[10,:,:,1]
# plt.figure()
# plt.imshow(np.log(np.abs(masked_kspace_cplx_n) + 1e-9),cmap="gray")
# plt.figure()
# plt.imshow(np.log(np.abs(cplx) + 1e-9),cmap="gray")
# plt.figure()
# plt.imshow(np.log(np.abs(cplx2) + 1e-9),cmap="gray")
#
# plt.show()


