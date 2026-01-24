package com.example.cuby.ui.room;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

    public class RoomBackgroundView extends View {
    private Paint ceilingSurfacePaint = new Paint();
    private Paint ceilingShadowPaint = new Paint();

    private Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint floorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ceilingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint windowLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint seamShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float floorY;
    private float lightShift = 0f;

    private Paint rugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint windowFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint windowGlassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint curtainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint woodPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bookPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RoomBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        floorY = h * 0.65f;
        float ceilingHeight = h * 0.18f;

        // --- PAINTS ---

        // Ceiling
        ceilingSurfacePaint.setShader(new LinearGradient(0, 0, 0, ceilingHeight, 0xFFF5F7FA, 0xFFE1E6EC, Shader.TileMode.CLAMP));
        ceilingShadowPaint.setShader(new LinearGradient(0, ceilingHeight - 20, 0, ceilingHeight + 10, 0x33000000, 0x00000000, Shader.TileMode.CLAMP));

        // Wall
        wallPaint.setShader(new LinearGradient(0, ceilingHeight, 0, floorY, new int[]{0xFFEAF4FF, 0xFFD8E6F3, 0xFFC4D6E8}, new float[]{0f, 0.6f, 1f}, Shader.TileMode.CLAMP));

        // Floor
        floorPaint.setShader(new LinearGradient(0, floorY, 0, getHeight(), new int[]{0xFFB6C9DD, 0xFF9FB5CC, 0xFF7F97AE}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));

        // Seam Shadow
        seamShadowPaint.setShader(new LinearGradient(0, floorY - 20, 0, floorY + 20, 0x44000000, 0x00000000, Shader.TileMode.CLAMP));

        // RUG (Blue/Teal)
        rugPaint.setColor(0xFFB3E5FC); // Light Blue Rug
        rugPaint.setStyle(Paint.Style.FILL);
        rugPaint.setShadowLayer(10, 0, 5, 0x33000000);

        // WINDOW
        windowFramePaint.setColor(0xFFFFFFFF);
        windowFramePaint.setStyle(Paint.Style.STROKE);
        windowFramePaint.setStrokeWidth(12f);
        
        windowGlassPaint.setShader(new LinearGradient(0, ceilingHeight + 50, 0, floorY - 200, 0xFFE3F2FD, 0xFFBBDEFB, Shader.TileMode.CLAMP));

        // CURTAINS (Blue-ish fabric)
        curtainPaint.setShader(new LinearGradient(0, 0, 50, 0, 
            new int[]{0xFFE1F5FE, 0xFF81D4FA, 0xFFE1F5FE}, 
            null, Shader.TileMode.MIRROR));

        // WOOD (Warm Oak)
        woodPaint.setColor(0xFF8D6E63);
        woodPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
             canvas.drawColor(0xFFE3ECF5);
             return;
        }

        float w = getWidth();
        float h = getHeight();
        float ceilingHeight = h * 0.18f;

        // 1. CEILING
        canvas.drawRect(0, 0, w, ceilingHeight, ceilingSurfacePaint);
        // Shadow
        canvas.drawRect(0, ceilingHeight - 20, w, ceilingHeight + 10, ceilingShadowPaint);

        // 2. WALL
        canvas.drawRect(0, ceilingHeight, w, floorY, wallPaint);

        // ... inside onDraw ...

        // 3. WINDOW (Centered on Wall)
        float windowW = w * 0.5f;
        float windowH = (floorY - ceilingHeight) * 0.5f;
        float windowX = (w - windowW) / 2;
        float windowY = ceilingHeight + 50;
        
        // AC UNIT (Above Window)
        float acW = w * 0.35f;
        float acH = 40f;
        float acX = (w - acW) / 2;
        float acY = ceilingHeight + 5; // Just below ceiling
        canvas.drawRoundRect(acX, acY, acX + acW, acY + acH, 10, 10, windowFramePaint); // Use white frame paint for AC
        canvas.drawLine(acX, acY + 30, acX + acW, acY + 30, windowFramePaint); // Vent line

        // Glass
        canvas.drawRect(windowX, windowY, windowX + windowW, windowY + windowH, windowGlassPaint);
        // Frame
        canvas.drawRect(windowX, windowY, windowX + windowW, windowY + windowH, windowFramePaint);
        // Crossbars
        canvas.drawLine(windowX + windowW/2, windowY, windowX + windowW/2, windowY + windowH, windowFramePaint);
        canvas.drawLine(windowX, windowY + windowH/2, windowX + windowW, windowY + windowH/2, windowFramePaint);

        // EXTRA: CURTAINS
        // Left
        canvas.drawRect(windowX - 40, windowY - 10, windowX + 40, windowY + windowH + 40, curtainPaint);
        // Right
        canvas.drawRect(windowX + windowW - 40, windowY - 10, windowX + windowW + 40, windowY + windowH + 40, curtainPaint);

        // ROD (Extended)
        canvas.drawLine(windowX - 60, windowY - 10, windowX + windowW + 60, windowY - 10, windowFramePaint);

        // DESK (Left Side)
        float deskW = w * 0.25f;
        float deskH = (floorY - ceilingHeight) * 0.4f;
        float deskY = floorY - deskH + 20; 
        canvas.drawRect(0, deskY, deskW, floorY + 10, woodPaint); // Main block
        
        // SHELF (Right Side)
        float shelfW = w * 0.2f;
        float shelfH = (floorY - ceilingHeight) * 0.7f;
        float shelfY = floorY - shelfH;
        canvas.drawRect(w - shelfW, shelfY, w, floorY, woodPaint); // Frame
        
        // Shelves & Books
        int[] bookColors = {0xFFEF5350, 0xFF66BB6A, 0xFF42A5F5, 0xFFFFCA28, 0xFFAB47BC};
        for(int i=1; i<4; i++) {
            float sy = shelfY + (shelfH/4)*i;
            canvas.drawLine(w - shelfW, sy, w, sy, windowFramePaint); // Shelf line
            
            // Draw some "books" on the shelf (random-ish rectangles)
            float currentX = w - shelfW + 10;
            int bColorIdx = i % bookColors.length;
            while(currentX < w - 10) {
                float bookW = 15 + (i*5) % 20; // Pseudo random width
                float bookH = 30 + (i*7) % 15; // Pseudo random height
                bookPaint.setColor(bookColors[bColorIdx++ % bookColors.length]);
                canvas.drawRect(currentX, sy - bookH, currentX + bookW, sy, bookPaint);
                currentX += bookW + 2;
                if(i % 2 == 0 && currentX > w - shelfW + 50) break; // Leave some space empty
            }
        }

        // 4. FLOOR
        canvas.drawRect(0, floorY, w, h, floorPaint);

        // 5. RUG (Centered on Floor)
        float rugW = w * 0.7f;
        float rugH = (h - floorY) * 0.6f;
        float rugX = (w - rugW) / 2;
        float rugY = floorY + 50;
        canvas.drawRoundRect(rugX, rugY, rugX + rugW, rugY + rugH, 30, 30, rugPaint);

        // Seam Shadow (Draw over rug's top edge slightly)
        canvas.drawRect(0, floorY - 20, w, floorY + 20, seamShadowPaint);
    }
}
