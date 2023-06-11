//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);


__first_login(player) -> (
  if(!scoreboard('first_login',player)
    ,scoreboard_add('first_login', 'dummy');
  );

  if(!scoreboard('first_login',player)
    ,inventory_set(player, 0, 1, 'warped_fungus_on_a_stick{CustomModelData:3}');
    scoreboard('first_login', player, 1);
  );

);

//scoreboard_add(clip);
__on_player_connects(player) -> (
  global_lastfired = time();
  global_clip = 6;
//  __first_login(player);


);

__on_player_uses_item(player, item_tuple, hand) -> (
  schedule(0, '__fire_gun', player, item_tuple, hand);
);

__fire_gun(player, item_tuple, hand) -> (
        trigger_timing = time() - global_lastfired;
        if(__holding_gun(player, item_tuple, hand) && inventory_find(player, 'iron_nugget') && trigger_timing > 700 && global_clip > 0 && !query(player, 'trace', 4.5, 'blocks')
                ,sound('entity.firework_rocket.large_blast', pos(player), 1, 0.5);
                if(!__has_infinity(item_tuple:2), inventory_remove(player, 'iron_nugget'); );
                if(query(player, 'trace',100,'entities')
                        ,badguy = query(player, 'trace',100,'entities');
                        badid = query(badguy, 'uuid');
                        badage = query(badguy, 'age');

                        run('damage '+badid+' 10 generic by '+player~'uuid');
                );

                global_lastfired = time();
                global_clip = global_clip-1;
                particle('campfire_cosy_smoke', pos_offset(pos(player), 'up', 2));

                ,if(__holding_gun(player, item_tuple, hand) && trigger_timing > 700,
                  display_title(player, 'actionbar', 'reload');
                  sound('item.flintandsteel.use', pos(player), 1, 0.5); 
                );

        );

);

__on_player_swings_hand(player, hand) -> (
  if(__holding_gun(player, query(player, 'holds', hand), hand) && inventory_find(player, 'iron_nugget') && !query(player, 'trace', 4.5, 'blocks')
    ,display_title(player, 'actionbar', 'reloading');
    global_clip = 6;
    sound('item.crossbow.loading_end', pos(player), 1, 0.5); 
    ,if(__holding_gun(player, query(player, 'holds', hand), hand) && !inventory_find(player, 'iron_nugget')
      ,display_title(player, 'actionbar', 'out of ammo');
     );
  );
);

__holding_gun(player, item_tuple, hand) -> (
  if(hand == 'mainhand' && item_tuple && item_tuple:0 =='warped_fungus_on_a_stick' && __has_tag(item_tuple:2)
    ,true
    ,false
  );
);


__has_tag(nbt) -> (
  data = parse_nbt(nbt):'CustomModelData';

  if(data != null
    ,data == '3';
    ,false
  );
);



__has_infinity(nbt) -> (
  enchants_list = parse_nbt(nbt):'Enchantments';

  if(enchants_list != null
    ,first(enchants_list, _:'id' == 'minecraft:infinity';) != null
    ,false
  );
);
