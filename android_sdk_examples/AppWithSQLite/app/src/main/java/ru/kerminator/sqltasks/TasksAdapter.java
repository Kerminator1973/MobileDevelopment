package ru.kerminator.sqltasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.kerminator.sqltasks.database.TaskEntry;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    // Массив данных, которые следует отобразить в списке
    private List<TaskEntry> mTaskEntries;

    //
    private Context mContext;

    // Определяем ссылку на обработчик события onListItemClick.
    // Названием типа может быть ListItemClickListener, или
    // ItemClickListener - это не играет большого значения
    final private ListItemClickListener mOnClickListener;

    // Определяем интерфейс для обработки события о выборе
    // пользователем элемента RecyclerView
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    // При конструировании объекта внешний код передаёт нам список данных и callback-метод,
    // в котором будет отрабатываться нажатие на элемент списка
    TasksAdapter( Context context, ListItemClickListener listener ) {
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Context context = parent.getContext();

        // Указываем Layout, который будет использован при создании новой
        // визуализации элемента списка. Используем контекст переданный извне
        // (в конструкторе класса), для того, чтобы мы получали события о
        // нажатии кнопки RecyclerView
        int layoutIdForListItem = R.layout.task_description;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Последний параметр - shouldAttachToParentImmediately устанавливаем
        // в значение false (типовое решение)
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        // Цель метода - создать новый ViewHolder и здесь мы его создаём
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

    public List<TaskEntry> getTasks() {
        return mTaskEntries;
    }

    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    // ViewHolder - это элемент (Layout) который используется при
    // формировании прокручиваемого списка. Другими словами, ViewHolder
    // это дочерний элемент списка, который, в свою очередь, состоит
    // из набора органов управления, таких как TextView. По сути,
    // ViewHolder описывает шаблоном элемента списка, в котором связывает
    // источник данных и органы управления из LinearLayout (в нашем
    // случае из файла "layout/task_description.xml"
    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView listItemNameView;
        TextView listItemUpdateAtView;

        TasksViewHolder(View itemView) {
            super(itemView);

            // Находим в указанном шаблоне View органы управления, предназначенные
            // для отображения информации о элементе списка. Эти ссылки будет
            // использоваться при инициализации визуализируемого элемента списка
            // с данными из источника данных (при связывании). См. bind()
            listItemNameView = itemView.findViewById(R.id.tv_item_task_name);
            listItemUpdateAtView = itemView.findViewById(R.id.tv_item_task_update_at);

            // Активируем обработку нажатий (touch) на экран
            itemView.setOnClickListener(this);
        }

        // Метод позволяет связать органы управления из LinearLayout
        // с полями источника данных
        void bind(int index) {

            TaskEntry task = mTaskEntries.get(index);

            listItemNameView.setText(task.getDescription());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            listItemUpdateAtView.setText(dateFormat.format(task.getUpdatedAt()));
        }

        @Override
        public void onClick(View v) {

            // Вызываем обработчик действия пользователя в Activity
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
