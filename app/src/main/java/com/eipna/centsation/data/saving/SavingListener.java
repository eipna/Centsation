package com.eipna.centsation.data.saving;

public interface SavingListener {
    void OnItemClick(int position);
    void OnUpdateClick(int position);
    void OnHistoryClick(int position);
    void OnArchiveClick(int position);
    void OnDeleteClick(int position);
}