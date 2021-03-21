
# PageIndicatorView

<img src="https://img.shields.io/badge/status-development-brightgreen"/>
<p>
    Animating Page indicator. It is mainly intended to work with ViewPager2
</p>

![Screenshot](https://github.com/AbrahamCuautle/PageIndicatorView/blob/main/images/image1.gif)

## Basic Usage

```xml
<com.abrahamcuautle.pageindicatorview.PageIndicatorView
        android:id="@+id/pageIndicatorView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:indicator_selected_color="@android:color/holo_green_light"
        app:indicator_unselected_color="@color/design_default_color_primary"
        app:indicator_radius="6dp"
        app:indicator_spacing="8dp"/>
```
Then call:

```
   pageIndicatorView.setPageIndicatorsCount(...)
   pageIndicatorView.selectPosition(...)
```
or use PageIndicatorMediator. There is no need to call ```setPageIndicatorsCount()```or ```selectPosition()``` but set an adapter before call ```PageIndicatorMediator.attach()```

```
    viewpager2.setAdapter(...)
    PageIndicatorMediator(viewpager2, pageIndicatorView).attach()
```
