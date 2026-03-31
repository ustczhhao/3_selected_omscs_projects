import h5py
import matplotlib.pyplot as plt
import numpy as np
import torch
import yaml
from types import SimpleNamespace
import cv2
import pickle
from scipy.io import loadmat, savemat
from fftc import *
import os

def get_filenames(dataset_dir):
    filenames=[]
    for filename in os.listdir(dataset_dir):
        if filename.endswith(".hdf5"):
            filenames.append(filename)
    return filenames

def get_args():
    with open('./config.yaml') as f:
        data = yaml.load(f, Loader=yaml.FullLoader)
    args = SimpleNamespace(**data)

    return args
args = get_args()
def fft2(img):
    return np.fft.fftshift(np.fft.fft2(img))

hf = h5py.File(args.train_dataset_path+"25.hdf5")
# label = hf["label"][()]
# print(label)
# label = np.array([label])

im = hf["im_gt"][()]
kspace = hf["kspace"][()]
# print(kspace)
# print(label.shape)
print(im.shape)
print(kspace.shape)
# im = hf["im_gt"][20]
# kspace = hf["kspace"][20]
# kspace_cplx = kspace[:,:,0]+1j*kspace[:,:,1]
#
# plt.figure()
# plt.imshow(np.log(np.abs(kspace_cplx) + 1e-9),cmap="gray")
# plt.figure()
# plt.imshow(im,cmap="gray")
# plt.show()

#

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


# if args.mask_type == 'radial' and args.sampling_percentage == 30:
#     print(mask_path+"radial/radial_30.tif")
#     with open(mask_path+"radial/radial_30.tif", 'rb') as pickle_file:
#         masks_dictionary = pickle.load(pickle_file)
#         # mask = torch.tensor(masks['mask1'] == 1, device=args.device)
#         masks = masks_dictionary
#         maskedNot = 1 - masks_dictionary
# elif args.mask_type == 'radial' and args.sampling_percentage == 50:
#     mask_shift = cv2.imread('.masks/radial/radial_50.tif', 0) / 255
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
# # print(masks.shape)
plt.figure()
plt.imshow(masks,cmap="gray")
# plt.figure()
# plt.imshow(maskedNot,cmap="gray")
# # plt.show()
#
masks = masks[np.newaxis,:,:,np.newaxis]
masked_kspace = masks*kspace
unsampled_im  = kspace2img(torch.from_numpy(masked_kspace),False)

# masked_kspace_n = masked_kspace[20]
# masked_kspace_cplx_n = masked_kspace_n[:,:,0]+1j*masked_kspace_n[:,:,1]
#
# plt.figure()
# plt.imshow(np.log(np.abs(masked_kspace_cplx_n) + 1e-9),cmap="gray")
#
# plt.show()
plt.figure()
plt.imshow(im,cmap="gray")
plt.figure()
plt.imshow(unsampled_im[0],cmap="gray")
plt.show()