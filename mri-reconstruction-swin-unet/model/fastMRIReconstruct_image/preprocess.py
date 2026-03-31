import h5py
import os
import numpy as np
import torch
from utils import *
from fftc import *
import pandas as pd

def get_filenames(dataset_dir):
    filenames=[]
    for filename in os.listdir(dataset_dir):
        filenames.append(filename)
    return filenames

dataset_path = r"G:\mrnet\val\\"
save_path = "./dataset/val/"
filenames = get_filenames(dataset_path)



counter = 0
for file in filenames:
    print("processing...{}".format(file))
    if not file.endswith(".npy"):
        continue
    im = np.load(dataset_path+file)
    im = im.astype(np.float32)
    im = norm(im)

    slice_n = im.shape[0]
    center_slice = slice_n//2



    kspace = img2kspace(torch.from_numpy(denorm(im[center_slice-5:center_slice+5])),transpose=False)
    img_cplx = kspace2img(kspace,transpose=False)
    kspace = kspace.numpy()
    im = im[center_slice-5:center_slice+5]
    for i in range(im.shape[0]):
        counter += 1

        with h5py.File(save_path+"{}.hdf5".format(counter), 'w') as f:
            f['kspace']=kspace[i] #[256,256,2]
            f['im_gt']=img_cplx[i]




