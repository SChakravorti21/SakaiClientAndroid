package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.sakaimobile.development.sakaiclient20.models.sakai.courses.CoursesResponse
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course
import org.junit.Test

class CourseDeserializerUnitTest {

    @Test
    fun deserializeSitePage() {
        val deserializer = GsonBuilder()
                .registerTypeAdapter(Course::class.java, CourseDeserializer())
                .create()
        val json = CourseDeserializerUnitTest::class.java.getResource("/courses/single_site_page.json")!!.readText()
        val course = deserializer.fromJson(json, Course::class.java)
        val sitePage = course.sitePages[0]
        assertThat(sitePage.title).isEqualTo("Assignments")
        assertThat(sitePage.siteId).isEqualTo("288289a0-8842-4c8c-ad84-6cdde9179e26")
        assertThat(sitePage.sitePageId).isEqualTo("e609a0d5-3745-4de5-b1d5-bc483dd2178b")
        assertThat(sitePage.url).isEqualTo("https://sakai.rutgers.edu/portal/site/288289a0-8842-4c8c-ad84-6cdde9179e26/page/e609a0d5-3745-4de5-b1d5-bc483dd2178b")
    }

    @Test
    fun deserializeCourseNoTerm() {
        val deserializer = GsonBuilder()
                .registerTypeAdapter(Course::class.java, CourseDeserializer())
                .create()
        val json = CourseDeserializerUnitTest::class.java.getResource("/courses/single_course_no_term.json")!!.readText()
        val course = deserializer.fromJson(json, Course::class.java)
        assertThat(course.term).isNotNull()
        assertThat(course.term.toString()).isEqualTo("General")
    }

    @Test
    fun deserializerMultipleCourses() {
        val deserializer = GsonBuilder()
                .registerTypeAdapter(Course::class.java, CourseDeserializer())
                .create()
        val json = CourseDeserializerUnitTest::class.java.getResource("/courses/real_courses.json")!!.readText()
        val coursesWrapper = deserializer.fromJson(json, CoursesResponse::class.java)
        assertThat(coursesWrapper).isNotNull()

        val courses = coursesWrapper.courses
        // Assure that there are exactly 22 courses as expected
        assertThat(courses.size).isEqualTo(22)
        // Ensure that sites have expected titles
        assertThat(courses.map { it.title })
                .containsExactly("250 Matlabs Spring 2018", "App Review",
                        "COMPUTER ARCHITECTUR 05 Sp18", "CS 112 - Fall 2017",
                        "CS214: Sys Prog (F18)", "Ghost in the Machine",
                        "HC MongoDB Site Visit '18", "Hindi Placement Exam",
                        "Honors Expos: HD", "INTERMEDIATE CHINESE 01 Sp18",
                        "INTERMEDIATE CHINESE 02 F17", "INTR DISCRET STRCT I 01 Sp18",
                        "INTRO LINEAR ALGEBRA C1 F18", "INTRO MUSIC THEORY 02 Sp19",
                        "INTRO TO SOCIOLOGY 19 Sp19", "IPE Faculty Materials",
                        "New Student Orientation Programs", "OPER SYSTEM DESIGN 01 Sp19",
                        "PRIN INFO & DATA MGT 01 Sp19", "Recommended Course Template",
                        "Test Site", "THE BYRNE SEMINARS 12 F17")
                .inOrder()
        // Ensure that none of the siteIds are null
        assertThat(courses.map { it.siteId }).doesNotContain(null)
        // Assure that each course has the correct number of site pages
        assertThat(courses.map { it.sitePages }.map { it.size })
                .containsExactly(5, 10, 8, 19, 8, 11, 5, 3, 13, 10, 9, 7, 9, 6, 9, 3, 3, 12, 7, 10, 12, 10)
                .inOrder()
    }

}