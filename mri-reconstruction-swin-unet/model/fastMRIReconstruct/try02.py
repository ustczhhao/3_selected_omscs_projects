import torch

import torch
import matplotlib.pyplot as plt

# Define parameters
Fs = 1000  # Sampling frequency
T = 1 / Fs  # Sampling interval
t = torch.arange(0, 1, T)  # Time vector
f = 5  # Frequency of the sine wave

# Create a sine wave signal
x = torch.sin(2 * torch.pi * f * t)

# Plot the sine wave
plt.figure(figsize=(10, 4))
plt.plot(t.numpy(), x.numpy())
plt.title('Original Signal (Sine Wave)')
plt.xlabel('Time (s)')
plt.ylabel('Amplitude')
plt.grid(True)
plt.show()

# Compute the FFT of the signal
X = torch.fft.fft(x)

# Compute the frequencies corresponding to FFT output
freqs = torch.fft.fftfreq(len(x), T)
print(freqs)
# Plot the FFT magnitude spectrum
plt.figure(figsize=(10, 4))
plt.plot(freqs.numpy(), torch.abs(X).numpy())
plt.title('FFT Magnitude Spectrum')
plt.xlabel('Frequency (Hz)')
plt.ylabel('Magnitude')
plt.grid(True)
plt.show()