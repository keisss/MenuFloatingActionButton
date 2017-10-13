package cn.keiss.menufab.listener;

import cn.keiss.menufab.view.MenuView;

/**
 * Created by hekai on 2017/10/13.
 * 菜单item点击监听
 */

public interface OnMenuItemClickListener {
    void onClick(MenuView view, int position);
}
