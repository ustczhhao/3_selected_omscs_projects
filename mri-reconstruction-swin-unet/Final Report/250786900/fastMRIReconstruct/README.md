# CS7643 Final Project Fast MRI Reconstruction
[ECCVW2022] The codes for the work "Swin-Unet: Unet-like Pure Transformer for Medical Image Segmentation"(https://arxiv.org/abs/2105.05537). A validation for U-shaped Swin Transformer. Our paper has been accepted by ECCV 2022 MEDICAL COMPUTER VISION WORKSHOP (https://mcv-workshop.github.io/). We updated the Reproducibility. I hope this will help you to reproduce the results.

## 1. Build environment
install torch, torchvision, tensorboard, cv2, matplotlib, scikit-image, einops, timm, pyyaml, etc.

## 2. Prepare data

-run preprocess.py to process MRNet data into h5py.

## 3. Train and test

- See main.py, run the corresponding method. Congigurations are in config.yaml.