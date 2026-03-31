import torch
import numpy as np
from skimage.metrics import structural_similarity as compare_ssim
from skimage.metrics import peak_signal_noise_ratio as compare_psnr
def norm(img):
    output = img/255.0  # 0,1
    output = 2 * (output - 0.5)  # -1,1

    return output

def denorm(img):
    output = 255.0 * (img / 2 + 0.5)
    return output


def save_model(model,step,file_path,optimizer=None):
    if optimizer==None:
        state = {
            "model":model.state_dict()
        }
    else:
        state = {
            "model":model.state_dict(),
            "optim":optimizer.state_dict()
        }
    torch.save(state,file_path+"-{}.pth".format(step))


def load_model(model, file_path, optimizer):
    print("load pretrained model from {}".format(file_path))
    prev_state = torch.load(file_path)
    model.load_state_dict(prev_state['model'])
    if optimizer != None:
        optimizer.load_state_dict(prev_state['optim'])


def evaluate_metrics(predict,groundtruth):
    #N,H,W
    N = predict.shape[0]
    psnr_list = []
    ssim_list = []
    for i in range(N):
        x = predict[i,:,:]
        y = groundtruth[i,:,:]
        psnr = compare_psnr(x, y,data_range=y.max()-y.min())
        ssim = compare_ssim(x, y,data_range=y.max()-y.min())
        psnr_list.append(psnr)
        ssim_list.append(ssim)

    psnr = np.average(np.array(psnr_list))
    ssim = np.average(np.array(ssim_list))
    return psnr, ssim

