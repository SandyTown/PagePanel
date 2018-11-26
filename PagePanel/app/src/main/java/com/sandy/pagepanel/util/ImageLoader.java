package com.sandy.pagepanel.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ImageLoader {
    private static final String TAG = ImageLoader.class.getName();


    private static ImageLoader instance;
    private LruCache<String, Bitmap> memoryCache;
    private Set<String> downloadingSet = new HashSet<>();

    private ExecutorService executor;

    private ImageLoader() {
        executor = Executors.newFixedThreadPool(15);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        long maxMemory = Runtime.getRuntime().maxMemory();

//        Logger.d(TAG, "maxMemory:" + maxMemory);

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = (int) (maxMemory / 12);
        if (cacheSize > 10 * 1024 * 1024) {
            cacheSize = 10 * 1024 * 1024;
        }
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount();
            }
        };
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }


    /**
     * @param imageView 图片控件
     * @param url       下载图片的地址
     */
    public synchronized void reLoad(final ImageView imageView, final String url, final boolean isCircle) {
        if (url == null || url.length() < 2) {
            return;
        }

        final Bitmap bitmap = getBitmapFromMemCache(url);
        if (bitmap != null) {
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            if (downloadingSet.contains(url)) {
                return;
            } else {
//                Logger.d(TAG, "reLoad url is:" + url);
                downloadingSet.add(url);
            }

            class MyHandler extends Handler {
                public void handleMessage(Message message) {
                    Bitmap bitmap = (Bitmap) message.obj;
                    if (bitmap != null) {
                        addBitmapToMemoryCache(url, bitmap);
                        if (imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    downloadingSet.remove(url);
                }
            }

            final MyHandler myHandler = new MyHandler();
            final Runnable runnable = new Runnable() {
                public void run() {
                    Bitmap bitmap = loadImageFromUrl(url, isCircle);
                    Message message = myHandler.obtainMessage(0, bitmap);
                    myHandler.sendMessage(message);
                }
            };

            executor.submit(runnable);
        }
    }


    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    private static Bitmap loadImageFromUrl(String url, boolean isCircle) {
        URL m;
        InputStream is;
        BufferedInputStream bis;
        ByteArrayOutputStream os;
        try {
            m = new URL(url);
            is = (InputStream) m.getContent();
            bis = new BufferedInputStream(is, 1024 * 8);
            os = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = bis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            bis.close();
            byte[] data = os.toByteArray();
            Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (isCircle) {
                int size = Math.min(source.getWidth(), source.getHeight());

                int x = (source.getWidth() - size) / 2;
                int y = (source.getHeight() - size) / 2;

                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                if (squaredBitmap != source) {
                    //回收垃圾
                    source.recycle();
                }

                Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
                Bitmap bitmap = Bitmap.createBitmap(size, size, config);

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                //定义一个渲染器
                BitmapShader shader = new BitmapShader(squaredBitmap,
                        BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                //设置渲染器
                paint.setShader(shader);
                //设置抗拒齿，图片边缘相对清楚
                paint.setAntiAlias(true);

                float r = size / 2f;
                //绘制图形
                canvas.drawCircle(r, r, r, paint);

                squaredBitmap.recycle();
                return bitmap;
            } else {
                return source;
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
