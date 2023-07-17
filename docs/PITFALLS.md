# Pitfalls

1) When we send code to ChatGPT the indentation will break if the code we send contains this chars ['\n', '\t', '\r'].
   To fix this we need to replace them with a space ' '.