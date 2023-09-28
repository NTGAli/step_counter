package com.ntg.stepcounter.util.extension



//
///**
// * Align fraction states into single value
// *
// *  1.0f - Expanded
// *  0.0f - Collapsed
// */
//@OptIn(ExperimentalMaterial3Api::class)
//val BottomSheetScaffoldState.currentFraction: Float
//    get() {
//        val fraction = bottomSheetState.
//        val targetValue = bottomSheetState.targetValue
//        val currentValue = bottomSheetState.currentValue
//
//        return when {
//            currentValue == SheetValue.PartiallyExpanded && targetValue == SheetValue.PartiallyExpanded -> 0f
//            currentValue == SheetValue.Expanded && targetValue == SheetValue.Expanded -> 1f
//            currentValue == SheetValue.PartiallyExpanded && targetValue == SheetValue.Expanded -> fraction
//            else -> 1f - fraction
//        }
//    }