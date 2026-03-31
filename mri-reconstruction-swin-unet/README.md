# MRI Reconstruction with Swin-Unet

This repository contains a deep learning project on accelerated MRI reconstruction using a Swin-Unet architecture. The project explores how transformer-based models can improve reconstruction quality from undersampled MRI data by capturing long-range dependencies more effectively than conventional CNN-based methods.

## Project Overview

Magnetic resonance imaging (MRI) is a critical non-invasive imaging modality in clinical diagnosis, but long acquisition times can lead to patient discomfort and motion artifacts. This project focuses on accelerating MRI reconstruction from undersampled data while preserving image quality.

To address the limitations of CNNs in modeling long-range spatial relationships, this project adopts a Swin-Unet framework that combines the hierarchical encoder-decoder design of U-Net with the global-context modeling capability of Swin Transformers.

## Key Highlights

- Developed and evaluated a Swin-Unet-based MRI reconstruction pipeline
- Compared reconstruction performance across frequency-domain, image-domain, and dual-domain strategies
- Demonstrated that image-domain reconstruction provided the strongest overall performance
- Achieved improved reconstruction quality over a U-Net baseline
- Explored reconstruction using complex-valued MR representations to preserve additional phase information

## Method Summary

The project investigated MRI reconstruction in three settings:

1. **Frequency-domain reconstruction**
   - Reconstructs undersampled k-space representations first, then converts them back to image space

2. **Image-domain reconstruction**
   - Converts undersampled k-space to image space and learns residual corrections directly in the image domain

3. **Dual-domain reconstruction**
   - Combines k-space reconstruction and image-domain refinement in a two-stage framework

The experiments showed that image-domain reconstruction was the most effective strategy.

## Results

On the validation set, the image-domain Swin-Unet achieved:

- **PSNR:** 30.8 ± 2.1
- **SSIM:** 0.90 ± 0.03

Compared with the U-Net baseline:

- **Baseline PSNR:** 29.1 ± 1.7
- **Baseline SSIM:** 0.84 ± 0.04

These results suggest that the transformer-based architecture improved texture recovery and reduced aliasing artifacts in reconstructed MRI images.

## Dataset

This project uses the **MRNet knee MRI dataset** from Stanford.

The dataset is **not included in this repository** and must be downloaded separately by the user due to dataset distribution and storage considerations.

Please obtain the dataset from the official source and place it in the appropriate data directory before running training or evaluation.

## Notes

- Dataset files are not provided in this repository
- Large intermediate outputs, pretrained weights, and raw MRI data should be managed separately
- This repository is intended to showcase the modeling pipeline, experimental design, and implementation structure

## Acknowledgment

This repository is based on a project on transformer-based MRI reconstruction and focuses on demonstrating model design, reconstruction strategy comparison, and practical implementation for accelerated medical imaging.