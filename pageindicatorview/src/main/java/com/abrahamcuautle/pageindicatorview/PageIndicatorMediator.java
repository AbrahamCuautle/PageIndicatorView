package com.abrahamcuautle.pageindicatorview;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

public class PageIndicatorMediator {

    private ViewPager2 viewPager;
    private PageIndicatorView pageIndicatorView;

    public PageIndicatorMediator(ViewPager2 viewPager, PageIndicatorView pageIndicatorView) {
        this.viewPager = viewPager;
        this.pageIndicatorView = pageIndicatorView;

    }

    public void attach() {
        this.viewPager.registerOnPageChangeCallback(new PageChangedListener());
        RecyclerView.Adapter adapter = this.viewPager.getAdapter();
        if (adapter != null) {
            adapter.registerAdapterDataObserver(new AdapterChangedListener());
        } else {
            throw new IllegalStateException(
                    "PageIndicatorMediator attached before ViewPager2 has an " + "adapter");
        }
        populatePageIndicatorView();
    }

    private void populatePageIndicatorView() {
        RecyclerView.Adapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            pageIndicatorView.setPageIndicatorsCount(adapter.getItemCount());
        }
    }

    private class AdapterChangedListener extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            populatePageIndicatorView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            populatePageIndicatorView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            populatePageIndicatorView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            populatePageIndicatorView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            populatePageIndicatorView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            populatePageIndicatorView();
        }
    }

    private class PageChangedListener extends ViewPager2.OnPageChangeCallback {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            pageIndicatorView.selectPosition(position, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    }


}
