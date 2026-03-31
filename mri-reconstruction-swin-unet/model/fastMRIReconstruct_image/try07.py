import torch
import torch.nn as nn
import torch.optim as optim

class FFTNet(nn.Module):
    def __init__(self):
        super(FFTNet, self).__init__()
        self.conv = nn.Conv2d(1, 1, kernel_size=3, padding=1)

    def forward(self, x):
        # 进行一些常规的卷积操作
        x = self.conv(x)
        # FFT 转换
        y = torch.fft.ifft2(x)
        return x,y

# 设定设备
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# 实例化模型和优化器
model = FFTNet().to(device)
optimizer = optim.Adam(model.parameters(), lr=0.001)

# 创建输入数据
x = torch.randn(1, 1, 256, 256, device=device, dtype=torch.float32, requires_grad=True)

# 前向传播
output,fft_output = model(x)
print(fft_output.requires_grad,fft_output.grad)
# 假设我们有一些目标复数数据（通常是预先计算好的或实验数据）
target = torch.randn_like(fft_output)

# 损失函数：计算复数域的均方误差
loss = torch.mean((fft_output.real - target.real) ** 2 + (fft_output.imag - target.imag) ** 2)

# 反向传播
optimizer.zero_grad()
loss.backward()
optimizer.step()

# 检查梯度
for name, param in model.named_parameters():
    if param.grad is not None:
        print(f"{name} gradient: {param.grad.norm()}")