package es.ffgiraldez.comicsearch.query.sugestion.ui


import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.nhaarman.mockitokotlin2.*
import es.ffgiraldez.comicsearch.comics.gen.suggestionsErrorViewState
import es.ffgiraldez.comicsearch.comics.gen.suggestionsResultViewState
import es.ffgiraldez.comicsearch.query.base.presentation.QueryViewState
import es.ffgiraldez.comicsearch.query.base.ui.toHumanResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions.assertEquals


class SuggestionBindingAdapterSpec : StringSpec({
    "FloatingSearchView should show progress on loading state" {
        val searchView = mock<FloatingSearchView>()

        searchView.bindSuggestions(QueryViewState.loading())

        verify(searchView).showProgress()

    }

    "FloatingSearchView should hide progress on non loading state" {
        val searchView = mock<FloatingSearchView>()

        searchView.bindSuggestions(QueryViewState.idle())

        verify(searchView).hideProgress()

    }

    "FloatingSearchView should not have interaction on null state" {
        val searchView = mock<FloatingSearchView>()

        searchView.bindSuggestions(null)

        verifyZeroInteractions(searchView)

    }

    "FloatingSearchView should show human description on error state" {
        checkAll(Arb.suggestionsErrorViewState()) { state ->
            val captor = argumentCaptor<List<SearchSuggestion>>()
            val searchView = mock<FloatingSearchView> {
                doNothing().whenever(it).swapSuggestions(captor.capture())
            }

            searchView.bindSuggestions(state)

            verify(searchView).swapSuggestions(any())

            with(captor.firstValue) {
                assertEquals(1, size)
                assertEquals(state._error.toHumanResponse(), get(0).body)
            }
        }
    }

    "FloatingSearchView should show results on result state" {
        checkAll(Arb.suggestionsResultViewState()) { state ->
            val captor = argumentCaptor<List<SearchSuggestion>>()
            val searchView = mock<FloatingSearchView> {
                doNothing().whenever(it).swapSuggestions(captor.capture())
            }

            searchView.bindSuggestions(state)

            verify(searchView).swapSuggestions(any())

            with(captor.firstValue) {
                assertEquals(state._results.size, size)
                assertEquals(state._results, this.map { it.body })
            }
        }
    }
})

