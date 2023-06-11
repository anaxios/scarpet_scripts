//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_player_uses_item(player, item_tuple, hand) -> (
    if(item_tuple:0 == 'bundle'
    ,   
        global_name = item_tuple:2:'display';
        //print(name);
        name_i = global_name:'Name';
        name_display = slice(split(':', name_i):1,1,-3);
        if(!name_display, name_display = 'bundle');
        inventory_set(player, query(player, 'selected_slot'), 1, 'bundle');
        screen = create_screen(player(),'generic_9x6', name_display, _(screen, player, action, data) -> (
            if(action == 'throw' && data:'slot' == player~'selected_slot'+81
            ,
                inventory_set(screen, data:'slot', 0);
                'cancel';
            );
            if(action == 'slot_update' && data:'slot' == player~'selected_slot'+81
            ,
                //print(data);
                inventory_set(screen, data:'slot', 1, 'bundle');
                inventory_set(screen, -1, 0);
                'cancel';
            );
            
            if(action == 'close'
            ,   
                tag = [];
                for(range(54)
                ,
                    i = inventory_get(screen, _);
                    if(i, 
                        if(i:2
                        ,tag += nbt('{Count: '+i:1+'b, id: "minecraft:'+i:0+'",' + 'tag:' + i:2 + '}');
                        ,tag += nbt('{Count: '+i:1+'b, id: "minecraft:'+i:0+'"' + '}');
                        );
                    );
                );
                begin_nbt = '{Items:[';
                end_nbt   =  '}';
                items = begin_nbt + str(join(',', tag)) + ']';
                if(global_name, items += ',display:' + global_name);
                //items += ',display:' + global_name;
                items += end_nbt;
                //print(items);
                if(tag
                ,    
                inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', (items));
                );
                sound('item.bundle.remove_one', pos(player));
            //print('closed');
            );
        ));
        
        task(_(outer(screen),outer(item_tuple),outer(player))->(
            if(screen_property(screen, 'open') == 'true'
            ,   
                //print(item_tuple:2:'Items');
                i = parse_nbt(item_tuple:2:'Items');
             //   print(i);
                if( i != 'null'
                , 
                    for(i
                    , 
                      if(_:'tag' == null,
                      inventory_set(screen, _i, _:'Count', _:'id');
                      //print('trueeee');
                      ,inventory_set(screen, _i, _:'Count', _:'id', encode_nbt(_:'tag') );
                      //print('naottrueeee');
                      );
                    );
                );
            );
        ));
    );
);