import torch
import numpy as np

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
