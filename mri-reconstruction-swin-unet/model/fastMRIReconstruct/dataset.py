from torch.utils.data import Dataset
import torch
import os
import h5py
import yaml
from types import SimpleNamespace
import cv2
import pickle
from scipy.io import loadmat, savemat
import numpy as np

def get_args():
    with open('./config.yaml') as f:
        data = yaml.load(f, Loader=yaml.FullLoader)
    args = SimpleNamespace(**data)

    return args
def get_filenames(dataset_dir):
    filenames=[]
    for filename in os.listdir(dataset_dir):
        if filename.endswith(".hdf5"):
            filenames.append(filename)
    return filenames

class MRNetDataset(Dataset):
  def __init__(self, args,):

      self.dataset_path = args.train_dataset_path
      self.dataset_filenames = get_filenames(self.dataset_path)
      self.dataset_size = len(self.dataset_filenames)
      print("we are loading datasets: there are {} samples".format(self.dataset_size))

      #code from SwinGan:https://github.com/learnerzx/SwinGAN
      if args.mask_path.endswith(".pickle"):
          with open(args.mask_path, 'rb') as pickle_file:
              masks_dictionary = pickle.load(pickle_file)
              self.masks = masks_dictionary['mask1']
              self.maskedNot = 1 - masks_dictionary['mask1']
      elif args.mask_path.endswith(".tif"):
          mask_shift = cv2.imread(args.mask_path, 0) / 255
          self.masks = mask_shift
          self.maskedNot = 1 - mask_shift
      elif args.mask_path.endswith(".mat"):
          masks_dictionary = loadmat(args.mask_path)
          try:
              self.masks = masks_dictionary['Umask']
              self.maskedNot = 1 - masks_dictionary['Umask']
          except:
              try:
                  self.masks = masks_dictionary['maskRS2']
                  self.maskedNot = 1 - masks_dictionary['maskRS2']
              except:
                  self.masks = masks_dictionary['population_matrix']
                  self.maskedNot = 1 - masks_dictionary['population_matrix']
      self.minmax_noise_val = args.minmax_noise_val
       ####

  def __len__(self):
    return self.dataset_size

  def __getitem__(self, index):

    file_path = self.dataset_path + self.dataset_filenames[index]
    hf = h5py.File(file_path)
    im_gt = hf["im_gt"][()]  #[256,256]
    kspace = hf["kspace"][()] #[256,256,2]

    masks = self.masks[ :, :, np.newaxis]
    maskednot = self.maskedNot[:,:,np.newaxis]
    masked_kspace = masks * kspace
    #add noise
    masked_kspace += np.random.uniform(low=self.minmax_noise_val[0], high=self.minmax_noise_val[1],
                                       size=masked_kspace.shape) * maskednot

    #reshape
    im_gt = im_gt[np.newaxis,:,:] #[slice,1,256,256]
    im_gt = im_gt.astype(np.float32)

    #normalize
    # im_gt /= 255.0 #0,1
    # im_gt = 2*(im_gt-0.5) #-1,1
    kspace_gt = kspace.transpose((2,0,1)) #[2,256,256]
    masked_kspace = masked_kspace.transpose((2,0,1))
    return {'masked_kspace': torch.from_numpy(masked_kspace).to(torch.float32), "kspace_gt":torch.from_numpy(kspace_gt).to(torch.float32),'im_gt': torch.from_numpy(im_gt).to(torch.float32)}