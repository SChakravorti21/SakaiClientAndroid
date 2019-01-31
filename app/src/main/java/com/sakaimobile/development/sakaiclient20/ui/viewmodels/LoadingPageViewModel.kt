package com.sakaimobile.development.sakaiclient20.ui.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository
import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository
import com.sakaimobile.development.sakaiclient20.repositories.GradeRepository
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoadingPageViewModel
@Inject constructor(_courseRepository: CourseRepository,
                    private val gradeRepository: GradeRepository,
                    private val announcementsRepository: AnnouncementRepository,
                    private val assignmentRepository: AssignmentRepository)
    : BaseViewModel(_courseRepository) {

    companion object {
        const val NUM_REFRESH_REQUESTS = 4
    }

    private val progressLiveData: MediatorLiveData<Int> = MediatorLiveData()

    fun getRefreshProgress(refreshCourses: Boolean) : LiveData<Int> {
        this.refreshAllData(refreshCourses)
        progressLiveData.value = 0
        return progressLiveData
    }

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

    override fun refreshAllData() {
        this.refreshGrades()
        this.refreshAnnouncements()
        this.refreshAssignments()
    }

    private fun refreshGrades() {
        this.gradeRepository.refreshAllGrades()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.io())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun refreshAnnouncements() {
        this.announcementsRepository.refreshAllAnnouncements()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.io())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun refreshAssignments() {
        this.assignmentRepository.refreshAllAssignments()
                .doOnSubscribe { compositeDisposable.add(it) }
                .doOnError { this.emitError(it) }
                .subscribeOn(Schedulers.io())
                .subscribe({ incrementProgress() }, { it.printStackTrace() })
    }

    private fun incrementProgress() {
        progressLiveData.postValue(progressLiveData.value?.plus(1))
    }

    override fun refreshSiteData(siteId: String) {
        throw NotImplementedError("This ViewModel is used to refresh _all_ data, not site data.")
    }
}
