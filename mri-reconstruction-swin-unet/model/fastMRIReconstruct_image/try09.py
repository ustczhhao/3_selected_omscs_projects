import numpy as np
import matplotlib.pyplot as plt
data_path = r"G:\迅雷下载\MRNet-v1.0\MRNet-v1.0\train\sagittal\0000.npy"
data_path2 = r"G:\mrnet\train\0000.npy"

im = np.load(data_path)
im2 = np.load(data_path2)

plt.figure()
plt.imshow(im[10],cmap="gray")
plt.figure()
plt.imshow(im2[10],cmap="gray")
plt.show()
