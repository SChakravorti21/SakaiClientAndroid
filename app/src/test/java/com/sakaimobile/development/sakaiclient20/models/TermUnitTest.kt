package com.sakaimobile.development.sakaiclient20.models

import org.junit.Test

import com.google.common.truth.Truth.assertThat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TermUnitTest {

    @Test
    fun term_basicConstruction() {
        val termEid = "2018:1"
        val term = Term(termEid)
        assertThat(term.year).isEqualTo(2018)
        assertThat(term.termInt).isEqualTo(1)
        assertThat(term.termEid).isEqualTo(termEid)
        assertThat(term.termString).isEqualTo("Spring")
    }

    @Test
    fun term_defaultToGeneral() {
        val termEid = "0000:0"
        val term = Term(termEid)
        assertThat(term.year).isEqualTo(0)
        assertThat(term.termInt).isEqualTo(0)
        assertThat(term.termString).isEqualTo("General")
    }

    @Test
    fun term_toString() {
        assertThat(Term("0000:0").toString()).isEqualTo("General")
        assertThat(Term("2018:3").toString()).isEqualTo("Spring 2018")
        assertThat(Term("2018:6").toString()).isEqualTo("Summer 2018")
        assertThat(Term("2018:9").toString()).isEqualTo("Fall 2018")
        assertThat(Term("2018:12").toString()).isEqualTo("Winter 2018")
    }

    @Test
    fun term_equals() {
        val nonTerm = Object()
        val termGeneral = Term("0000:0")
        val termSpringEid = "2018:3"
        val termSpring2018 = Term(termSpringEid)
        val termSpring2019 = Term("2019:3")
        assertThat(termGeneral == null).isFalse()
        assertThat(termGeneral == nonTerm).isFalse()
        assertThat(termGeneral == termSpring2018).isFalse()
        assertThat(termSpring2018 == termSpring2019).isFalse()
        assertThat(termSpring2018 == Term(termSpringEid)).isTrue()
    }

    @Test
    fun term_compareTo() {
        val term = Term("2018:6")
        val sameTerm = Term("2018:6")
        val lowerTermYear = Term("2017:6")
        val lowerTermSemester = Term("2018:3")
        val higherTermYear = Term("2019:6")
        val higherTermSemester = Term("2018:9")

        assertThat(term.compareTo(sameTerm)).isEqualTo(0)
        assertThat(term.compareTo(lowerTermYear)).isGreaterThan(0)
        assertThat(term.compareTo(lowerTermSemester)).isGreaterThan(0)
        assertThat(term.compareTo(higherTermYear)).isLessThan(0)
        assertThat(term.compareTo(higherTermSemester)).isLessThan(0)
    }

}
