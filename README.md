# Monitor
Android无埋点数据收集框架。

参考[博文](https://juejin.im/post/58ec8558a22b9d006340531d)技术点，使用ASM字节码插桩实现具体埋点功能。使用主要由下面的架包完成。

## LibMonitor
具体实现埋点工具类。当前版本![LibMonitor Version](https://img.shields.io/badge/release-1.0.0-brightgreen.svg)

## buildSrc
gradle插件，实现字节码插桩。当前版本![buildSrc Version](https://img.shields.io/badge/release-1.0.0-brightgreen.svg)


插桩前代码

```java
protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textView = (TextView) findViewById(R.id.text);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

```

插桩后
```java
protected void onCreate(@Nullable Bundle savedInstanceState) {
        TraceUtil.onActivityCreate(this);
        super.onCreate(savedInstanceState);
        this.setContentView(2131361820);
        TextView textView = (TextView)this.findViewById(2131230861);
        Button button1 = (Button)this.findViewById(2131230748);
        Button button2 = (Button)this.findViewById(2131230749);
        Button button3 = (Button)this.findViewById(2131230750);
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                TraceUtil.onActivityClick(var1);
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                TraceUtil.onActivityClick(var1);
            }
        });
    }
```
可以看到插桩后```onCreate```方法中多了```TraceUtil.onActivityCreate(this);``` ; ```onClick```中多了```TraceUtil.onActivityClick(var1)```,即达到效果。
         
