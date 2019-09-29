package maelumat.almuntaj.abdalfattah.altaeb.utils;

import android.content.DialogInterface;

public interface QuestionActionListeners {
    void onPositiveFeedback(QuestionDialog dialog);
    void onNegativeFeedback(QuestionDialog dialog);
    void onAmbiguityFeedback(QuestionDialog dialog);
    void onCancelListener(DialogInterface dialog);
}