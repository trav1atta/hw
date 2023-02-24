package com.example.hw;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // создание полей для вывода на экран нужных значений
    private Button buttonStart; // кнопка запуска секундомера
    private Button buttonPause; // кнопка паузы секундомера
    private Button buttonReverse; // кнопка остановки секундомера
    private TextView stopwatchOut; // поле вывода результирующей информации

    // дополнительное поля логики
    private long startTime = 0L; // стартовое время
    private long timeInMilliseconds = 0L; // текущее время в миллисекундах
    private long timePause = 0L; // время в состоянии "Пауза"
    private long updatedTime = 0L; // обновлённое время
    private boolean timeMod = false;
    /**
     * К потоку (thread) программы нам нужно привязать очередь сообщений
     * Мы можем указать, чтобы сообщение ушло на обработку не сразу, а спустя определенное кол-во времени
     * Нам пригодится обработчик Handler
     * Handler - это механизм, который позволяет работать с очередью сообщений
     * Он привязан к конкретному потоку (thread) и работает с его очередью
     * Handler умеет помещать сообщения в очередь, при этом он ставит самого себя в качестве получателя этого сообщения
     * И когда приходит время, система достает сообщение из очереди и отправляет его адресату (т.е. в Handler) на обработку
     */
    private Handler handler = new Handler(); // обработчик очереди сообщений

    // вывод на экран полученных значений
    @Override
    protected void onCreate(Bundle savedInstanceState) { // создание жизненного цикла активности
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // присваивание жизненному циклу активити представления activity_main

        // присваивание переменным активити элементов представления activity_main
        buttonStart = findViewById(R.id.buttonStart); // кнопка обработки
        buttonPause = findViewById(R.id.buttonPause); // кнопка обработки
        buttonReverse=findViewById(R.id.buttonReverse); // кнопка обработки
        stopwatchOut = findViewById(R.id.stopwatchOut); // поле вывода

        // выполнение действий при нажании кнопки
        buttonStart.setOnClickListener(listener); // обработка нажатия кнопки
        buttonPause.setOnClickListener(listener); // обработка нажатия кнопки
        buttonReverse.setOnClickListener(listener); // обработка нажатия кнопки
    }

    // объект обработки нажатия всех кнопок
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { // при нажатии кнопки во view записывается значение кнопки
            switch (view.getId()) { // switch (значение выбранного ID кнопки)
                case R.id.buttonStart: // нажатие кнопки "Старт"
                    startTime = SystemClock.uptimeMillis(); // Миллисекунды с момента загрузки (не считая времени, проведенного в глубоком сне)
                    handler.postDelayed(updateTimerThread, 0); // запуск потока с нулевой задержкой
                    timeMod = true;
                    break;
                case R.id.buttonPause: // нажатие кнопки "Пауза"
                    timePause += timeInMilliseconds; // фиксирование времени в момент нажатия кнопки
                    handler.removeCallbacks(updateTimerThread); // удаление из очереди данного потока
                    break;
                case R.id.buttonReverse: // нажатие кнопки "Стоп"
                    timeMod = false;
                    startTime = SystemClock.uptimeMillis(); // Миллисекунды с момента загрузки (не считая времени, проведенного в глубоком сне)
                    handler.postDelayed(updateTimerThread, 0);
                    break;
            }
        }
    };

    // создание нового потока для обновления времени с помощью объекта интерфейса Runnable
    private Runnable updateTimerThread = new Runnable() {
        public void run() { // внутри метода run() помещается код выполняемого потока
            if (timeMod) {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = timePause + timeInMilliseconds;
            } else{
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime; //
                updatedTime = timePause - timeInMilliseconds;
            }
            int milliseconds = (int) (updatedTime % 1000); // определение количества миллисекунд
            int second = (int) (updatedTime / 1000); // определение количества секунд
            int minute = second / 60; // определение количества минут
            int hour = minute / 60; // определение количества часов
            int day = hour / 24; // определение количества дней

            second = second % 60; // ограничение количества секунд 60 секундами
            minute = minute % 60; // ограничение количества минут 60 минутами
            hour = hour % 24; // ограничение количества часов 24 часами

            // запись времени в окне вывода информации
            stopwatchOut.setText("" + day + ":" + hour + ":" + minute + ":" + String.format("%02d", second) + ":" + String.format("%03d", milliseconds));
            handler.postDelayed(this, 0); // запуск потока с нулевой задержкой
        }
    };
}

