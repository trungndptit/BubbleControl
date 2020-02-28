package com.example.bubblecontrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bubblecontrol.bubbleSource.BubbleLayout;
import com.example.bubblecontrol.bubbleSource.BubblesManager;
import com.example.bubblecontrol.bubbleSource.ExpandedLayout;
import com.example.bubblecontrol.bubbleSource.OnInitializedCallback;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private BubblesManager bubblesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        initializeBubblesManager();

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBubble();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeBubblesManager();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addNewBubble() {
        final BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null);
        final ExpandedLayout expandedLayoutLeft = (ExpandedLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.expanded_left_layout, null);
        final ExpandedLayout expandedLayoutRight = (ExpandedLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.expanded_right_layout, null);

        final View collapsedView = bubbleView.findViewById(R.id.collapse_view);
        final View expandedView = expandedLayoutLeft.findViewById(R.id.expand_container);

        final View expandedViewLeft01 = expandedLayoutLeft.findViewById(R.id.iv_x);
        final View expandedViewRight01 = expandedLayoutRight.findViewById(R.id.iv_x);
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


                System.out.println("Debug Collapse: " + x +" " + y);

                collapsedView.setVisibility(View.GONE);
                if (x < 200){
                    bubblesManager.addExpanded(expandedLayoutLeft, x, y-320);
                } else {
                    bubblesManager.addExpanded(expandedLayoutRight, x, y-300);
                }

                int w = expandedLayoutLeft.getWidth();
                int h = expandedLayoutLeft.getHeight();

                System.out.println("Debug expanded: " + w +" " + h);

                bubbleView.setIsExpanded(true);
                Toast.makeText(getApplicationContext(), "Clicked !",
                        Toast.LENGTH_SHORT).show();
            }
        });
        expandedViewLeft01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                bubblesManager.removeExpanded(expandedLayoutLeft);
            }
        });
        expandedViewRight01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                bubblesManager.removeExpanded(expandedLayoutRight);
            }
        });

        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    // Config BubbleManager + set TrashLayout + add a new bubble
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
