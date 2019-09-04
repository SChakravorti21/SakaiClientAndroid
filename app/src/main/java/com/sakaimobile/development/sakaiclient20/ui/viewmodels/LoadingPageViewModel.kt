package com.sakaimobile.development.sakaiclient20.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository
import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository
import com.sakaimobile.development.sakaiclient20.repositories.GradeRepository
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Shoumyo Chakravorti.
 *
 * The ViewModel that backs the loading screen {@see LoadingActivity}.
 * The only LiveData exposed by this ViewModel is one denoting the
 * completion progress for refreshing all data. Every refresh action
 * increments the number of requests completed successfully
 */

class LoadingPageViewModel
@Inject constructor(_courseRepository: CourseRepository,
                    private val gradeRepository: GradeRepository,
                    private val announcementsRepository: AnnouncementRepository,
                    private val assignmentRepository: AssignmentRepository)
    : BaseViewModel(_courseRepository) {

    companion object {
        // The number of network requests made to refresh all data
        // INCREMENT THIS VALUE IF MORE DATA NEEDS TO BE REFRESHED INSIDE refreshAllData()
        const val NUM_REFRESH_REQUESTS = 4
    }

    private val progressLiveData: MutableLiveData<Int> = MutableLiveData()

    /**
     * Returns a LiveData that represents the number of network requests
     * completed successfully (up to a maximum of NUM_REFRESH_REQUESTS).
     */
    fun getRefreshProgress(refreshCourses: Boolean) : LiveData<Int> {
        this.refreshAllData(refreshCourses)
        progressLiveData.value = 0
        return progressLiveData
    }

    /**
     * First refreshes the user's courses if necessary, then updates all other
     * major entities. Courses are refreshed first to avoid foreign key constraint failures
     * (all the major entities - grades, announcements, assignments - have an FK
     * pointing to a course).
     */
    private fun refreshAllData(refreshCourses: Boolean) {
        if(refreshCourses) {
            this.courseRepository.refreshAllCourses()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.incrementProgress()
                    this.refreshAllData()
                }, { it.printStackTrace() })
        } else {
            this.incrementProgress()
            this.refreshAllData()
        }
    }

    /**
     * Refreshes the grades, announcements, and assignments in parallel
     * and updates the progress accordingly
     */
    override fun refreshAllData() {
        this.refreshGrades()
        this.refreshAnnouncements()
        this.refreshAssignments()
    }

    //----------------------------
    //      UPDATE FUNCTIONS
    //-----------------------------

    private fun refreshGrades() {
        this.gradeRepository.refreshAllGrades()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun refreshAnnouncements() {
        this.announcementsRepository.refreshAllAnnouncements()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun refreshAssignments() {
        this.assignmentRepository.refreshAllAssignments()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun incrementProgress() {
        // postValue is thread-safe, in case multiple network requests complete simultaneously
        progressLiveData.postValue(progressLiveData.value?.plus(1))
    }

    override fun refreshSiteData(siteId: String) {
        throw NotImplementedError("This ViewModel is used to refresh _all_ data, not site data.")
    }
}
