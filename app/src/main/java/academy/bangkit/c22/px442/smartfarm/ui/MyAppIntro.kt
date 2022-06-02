package academy.bangkit.c22.px442.smartfarm.ui

import academy.bangkit.c22.px442.smartfarm.R
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

class MyAppIntro : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransformer(AppIntroPageTransformerType.Fade)
        isColorTransitionsEnabled = true
        isIndicatorEnabled = true

        setIndicatorColor(
            selectedIndicatorColor = ContextCompat.getColor(this, R.color.dark_grey),
            unselectedIndicatorColor = ContextCompat.getColor(this, R.color.light_grey)
        )

        showStatusBar(true)

        addSlide(AppIntroFragment.createInstance(
            description = "SmartFarm is here as a solution to achieve the goal of precision farming",
            imageDrawable = R.drawable.slide_1,
            descriptionColorRes = R.color.dark_grey,
        ))

        addSlide(AppIntroFragment.createInstance(
            description = "The goal of precision farming is to improve quality, quantity and save costs",
            imageDrawable = R.drawable.slide_2,
            descriptionColorRes = R.color.dark_grey,
        ))

        addSlide(AppIntroFragment.createInstance(
            description = "Let's be a smart farmer with SmartFarm App",
            imageDrawable = R.drawable.slide_3,
            descriptionColorRes = R.color.dark_grey,
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}