package com.eipna.centsation.data.saving;

public interface SavingListener {
    void OnClick(int position);
    void OnOperationClick(SavingOperation operation, int position);
}