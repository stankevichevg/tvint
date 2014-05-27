package com.evst.tvint.testutil

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar

/**
 * User: EStankevich
 * Date: 24.05.2014
 */
@RunWith(classOf[JUnitRunner])
abstract class Spec extends WordSpec with MustMatchers with MockitoSugar
