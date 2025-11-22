package com.elnfach.realms.gen

import com.elnfach.ui.R

class MarkovStoryGen {

    private enum class TransitionType
    {
        Start,
        Location,
        Character,
        Quest,
        Obstacle,
        Reward,
    }

    private enum class TransitionElement
    {
        Location,
        Character,
        Event,
        Quest,
        Obstacle,
        Reward,
        Dialogue,
        Conflict,
        Choice,
        Solution,
        Failure,
        Alternative,
        End,
        NewQuest,
        Complication
    }

    private val transitionProbabilities = mapOf(
        TransitionType.Start to listOf(
            TransitionElement.Location to 0.4,
            TransitionElement.Character to 0.4,
            TransitionElement.Event to 0.2
        ),
        TransitionType.Location to listOf(
            TransitionElement.Character to 0.3,
            TransitionElement.Event to 0.4,
            TransitionElement.Obstacle to 0.3
        ),
        TransitionType.Character to listOf(
            TransitionElement.Quest to 0.5,
            TransitionElement.Dialogue to 0.3,
            TransitionElement.Conflict to 0.2
        ),
        TransitionType.Quest to listOf(
            TransitionElement.Obstacle to 0.4,
            TransitionElement.Reward to 0.3,
            TransitionElement.Choice to 0.3
        ),
        TransitionType.Obstacle to listOf(
            TransitionElement.Solution to 0.6,
            TransitionElement.Failure to 0.2,
            TransitionElement.Alternative to 0.2
        ),
        TransitionType.Reward to listOf(
            TransitionElement.End to 0.5,
            TransitionElement.NewQuest to 0.3,
            TransitionElement.Complication to 0.2
        )
    )

    /*private val elements = mapOf(
        TransitionElement.Location to listOf(R.string.deep_forest)
    )*/
}