import torch
import torch.nn as nn
import torch.optim as optim
import  numpy as np
import  matplotlib.pyplot as plt
import os
def get_filenames(dataset_dir):
    filenames=[]
    for filename in os.listdir(dataset_dir):
        filenames.append(filename)
    return filenames

dataset_path = "./mrnet/train/"
filenames = get_filenames(dataset_path)

filename = filenames[1]
print(filename)


im = np.load(dataset_path+filename)
print(im.shape)
plt.figure()
plt.imshow(im[26],cmap="gray")
plt.show()