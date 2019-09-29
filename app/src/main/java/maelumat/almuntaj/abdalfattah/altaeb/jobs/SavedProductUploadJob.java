package maelumat.almuntaj.abdalfattah.altaeb.jobs;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIClient;

/**
 * Created by jayanth on 22/2/18.
 */

public class SavedProductUploadJob extends JobService {
    OpenFoodAPIClient apiClient;

    @Override
    public boolean onStartJob(JobParameters job) {
        apiClient = new OpenFoodAPIClient(this);
        apiClient.uploadOfflineImages(this, false, job, this);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        apiClient.uploadOfflineImages(this, true, job, this);
        return true;
    }

}
