package com.geekbrains.city_weather.frag;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.geekbrains.city_weather.DetailActivity;
import com.geekbrains.city_weather.R;
import com.geekbrains.city_weather.adapter.RecyclerViewCityAdapter;
import com.geekbrains.city_weather.dialogs.DialogCityAdd;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.geekbrains.city_weather.constants.AppConstants.CITY_MARKED;
import static com.geekbrains.city_weather.constants.AppConstants.CURRENT_CITY;
import static com.geekbrains.city_weather.constants.AppConstants.CURRENT_CITY_MARKED;
import static com.geekbrains.city_weather.constants.AppConstants.WEATHER_FRAFMENT_TAG;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChooseCityFrag extends Fragment {
    private static final String TAG = "33333";
    private String city = "";
    private boolean isExistWhetherFrag;  // Можно ли расположить рядом фрагмент с погодой
    private RecyclerView recyclerViewMarked; //RecyclerView для списка ранее выбранных городов
    private ArrayList<String> cityMarked = new ArrayList<>(); //список ранее выбранных городов
    private RecyclerViewCityAdapter recyclerViewCityAdapter; //адаптер для RecyclerView

    public ChooseCityFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_choose, container, false);
    }

    //проверка - если такой город есть в списке- возвращает false
    //сделан статическим, чтобы можно было использовать в адаптере списка
    public static boolean isNotCityInList(String city, ArrayList<String> cityMarked) {
        for (int i = 0; i < cityMarked.size(); i++) {
            if (cityMarked.get(i).toUpperCase().equals(city.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        //initRecycledView();
        registerForContextMenu(recyclerViewMarked);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewCityAdapter.notifyDataSetChanged();
    }

    // Сохраним текущий город (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CURRENT_CITY, city);
        outState.putStringArrayList(CURRENT_CITY_MARKED, cityMarked);
        Log.d(TAG, "ChooseCityFrag savedInstanceState cityMarked.size()= " +
                cityMarked.size() + " city = " + city);
        super.onSaveInstanceState(outState);
    }

    //************************************************************************************
    //Действия по подключению контекстного меню для пунктов списка RecyclerView во фрагменте
    // 1 в onViewCreated фрагмента пишем registerForContextMenu(recyclerViewMarked);
    // 2 делаем метод onContextItemSelected(MenuItem item) как обычно (см ниже)
    // 3 ViewHolder адаптера implements View.OnCreateContextMenuListener и реализуем
    //onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) интерфейса
    // 4 присваиваем слушатель адаптеру во ViewHolder: itemView.setOnCreateContextMenuListener(this);
    // 5  устанавливаем слушатель для долгих нажатий в onBindViewHolder адаптера
    // holder.textView.setOnLongClickListener(new View.OnLongClickListener()
    //*******************************************************************

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        handleMenuItemClick(item);
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом данные в другом фрагменте
        isExistWhetherFrag = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущий город
            city = savedInstanceState.getString(CURRENT_CITY);
            //восстанавливаем список выбранных городов
            cityMarked = savedInstanceState.getStringArrayList(CURRENT_CITY_MARKED);
            //adapter.notifyDataSetChanged() не работает, придётся так
            this.initRecycledView(); //если не сделать, при повороте теряем список
            Log.d(TAG, "ChooseCityFrag onActivityCreated cityMarked.size()= " +
                    cityMarked.size() + " city = " + city);
        }

        // Если можно нарисовать рядом данные, то сделаем это
        if (isExistWhetherFrag) {
            showCityWhether(city);
        }
    }

    //инициализация View
    private void initViews(View view) {
        recyclerViewMarked = view.findViewById(R.id.recycledViewMarked);
        CheckBox checkBoxWind = view.findViewById(R.id.checkBoxWind);
        checkBoxWind.setChecked(true);
        checkBoxWind.setEnabled(false);
        CheckBox checkBoxPressure = view.findViewById(R.id.checkBoxPressure);
        checkBoxPressure.setChecked(true);
        checkBoxPressure.setEnabled(false);
    }

    //инициализация RecycledView
    private void initRecycledView() {
        //используем встроенный LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //реализуем интерфейс адаптера, в  его методе onCityClick получим имя города и его позицию
        RecyclerViewCityAdapter.OnCityClickListener onCityClickListener =
                new RecyclerViewCityAdapter.OnCityClickListener() {
                    @Override
                    public void onCityClick(String newCity) {
                        //изменяем город
                        city = newCity;
                        // показываем погоду в городе с учётом ориентации экрана
                        showCityWhetherWithOrientation(city);
                    }
                };
        //передадим адаптеру в конструкторе список выбранных городов и ссылку на интерфейс
        //в принципе, надо через adapter.setOnCityClickListener, но хочу попробовать так
        //понятно, что это  неуниверсально, так как адаптер теперь зависит от конкретного интерфейся
        recyclerViewCityAdapter = new RecyclerViewCityAdapter(cityMarked, onCityClickListener,
                getActivity());

        recyclerViewMarked.setLayoutManager(layoutManager);
        recyclerViewMarked.setAdapter(recyclerViewCityAdapter);
    }

    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    private void handleMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_add: {
                DialogFragment dialogFragment = new DialogCityAdd();
                dialogFragment.show(Objects.requireNonNull(getFragmentManager()), "addCity");
                break;
            }
            case R.id.menu_remove: {
                recyclerViewCityAdapter.removeElement();
                break;
            }
            case R.id.menu_clear: {
                recyclerViewCityAdapter.clearList();
                break;
            }
            case R.id.menu_cancel: {
                //TODO
                break;
            }
        }
    }

    // Показать погоду во фрагменте рядом со спиннером в альбомной ориентации
    private void showCityWhether(String city) {

        Log.d(TAG, "showCityWhether  isExistWhetherFrag =  " + isExistWhetherFrag);
        // Проверим, что фрагмент с погодой существует в activity - обращение по id фрагмента
        WeatherFragment weatherFrag = (WeatherFragment)
                Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.whether_in_citys);

        //для отладки
        if (weatherFrag != null) {
            Log.d(TAG, "weatherFrag.getCity() = " + weatherFrag.getCity());
        }
        // создаем новый фрагмент с текущей позицией для вывода погоды
        weatherFrag = WeatherFragment.newInstance(city, cityMarked);
        // ... и выполняем транзакцию по замене фрагмента
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.whether_in_citys, weatherFrag, WEATHER_FRAFMENT_TAG);  // замена фрагмента
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);// эффект
        ft.commit();
    }

    // показываем погоду в городе с учётом ориентации экрана
    private void showCityWhetherWithOrientation(String city) {
        //если альбомная ориентация,то
        if (isExistWhetherFrag) {
            showCityWhether(city);
            //а если портретная, то
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(CURRENT_CITY, city);
            intent.putExtra(CITY_MARKED, cityMarked);
            startActivity(intent);
        }
    }

    //получаем актуальное значение currentPosition и cityMarked при перевороте экрана в DetailActivity
    //хотя в погодном приложении работает и без этого метода - обновление же по кнопке
    public void getCurrentPositionAndList(String currentCity, ArrayList<String> cityMarked) {
        city = currentCity;
        this.cityMarked = cityMarked;
        if (isNotCityInList(city, cityMarked)) {
            //если нет, добавляем его
            cityMarked.add(city); //добавляем город в список ранее выбранных городов
        }
        this.initRecycledView();
    }

    public void prepareData(String city) {
        this.city = city;
        //проверяем есть ли город в списке cityMarked
        if (isNotCityInList(city, cityMarked)) {
            //если нет, добавляем его
            cityMarked.add(city); //добавляем город в список ранее выбранных городов
        }
        Log.d(TAG, "cityMarked.add(city) cityMarked.size() = " + cityMarked.size());
        recyclerViewCityAdapter.notifyDataSetChanged(); // - перерисует сразу весь список
        Toast.makeText(getActivity(), city, Toast.LENGTH_LONG).show();
        // показываем погоду в городе с учётом ориентации экрана
        showCityWhetherWithOrientation(city);
    }

    public void addNewCity(String city) {
        recyclerViewCityAdapter.addItem(city);
    }
}

