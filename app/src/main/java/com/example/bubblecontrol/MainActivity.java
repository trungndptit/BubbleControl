package com.example.bubblecontrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bubblecontrol.bubbleSource.BubbleLayout;
import com.example.bubblecontrol.bubbleSource.BubblesManager;
import com.example.bubblecontrol.bubbleSource.OnInitializedCallback;

public class MainActivity extends AppCompatActivity {

    private BubblesManager bubblesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeBubblesManager();

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBubble();
            }
        });
    }

    private void addNewBubble() {
        final BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null);
//        final BubbleLayout expandedRightView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.expanded_right_layout, null);
        final View collapsedView = bubbleView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = bubbleView.findViewById(R.id.expand_container);
        final View expandedRightView1 = bubbleView.findViewById(R.id.expand_container_right);

//        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null);
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
            }
        });
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                int[] location = new int[2];
                collapsedView.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];

                collapsedView.setVisibility(View.GONE);
                if (x < 200){
                    expandedView.setVisibility(View.VISIBLE);
                } else {
                    expandedRightView1.setVisibility(View.VISIBLE);
                }

                bubbleView.setIsExpanded(true);
                Toast.makeText(getApplicationContext(), "Clicked !",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExpandedClick(BubbleLayout bubble) {
                collapsedView.setVisibility(View.VISIBLE);
                if (expandedView.getVisibility() == View.VISIBLE){
                    expandedView.setVisibility(View.GONE);
                } else{
                    expandedRightView1.setVisibility(View.GONE);
                }
                int[] location = new int[2];
                expandedView.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                System.out.println("Debug view: " + x + " " + y);
            }
        });
        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    private void initializeBubblesManager() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        addNewBubble();
                    }
                })
                .build();
        bubblesManager.initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }
}
