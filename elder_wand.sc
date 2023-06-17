//!scarpet v1.5
// import('links'
//       , 'global_links'
//       , '__link_part_new'
//       , '__link_new'
//       , '__link_part_match'
//       , '__link_match'
//       , '__link_store'
//       , '__to_disk'
//       , '__to_global_links'
//       , '__link_part_global_links_match'
// );
// stay loaded
__config() -> {
    'command_permission' -> 'ops', 
    'stay_loaded' -> 'true',

    'commands' -> {
        '' -> _() -> print('USAGE: "/elder_wand wand" to give wand'),
        'wand' -> _() -> (if(player()~'gamemode' == 'creative',
                          run('give @p minecraft:stick{Enchantments:[{id:"minecraft:luck",lvl:1}]} 1'),
                          print(format('r ERROR: Only creative players can use this command')))),
    }
};

__on_start() -> (
  link_file = read_file('links', 'json');
  if(link_file
  , global_links = (link_file);
  , global_links = [];
  );

  global_part1 = __link_part_new(null,null);
  global_player_coord = __link_part_new(pos(player()), player()~'dimension'); 
);

__on_player_connects(player) -> (
  global_player_coord = __link_part_new(pos(player), player~'dimension'); 
);

__on_player_starts_sneaking(player) -> (
    p = player();
    underfoot = pos_offset(pos(p), 'down', 1);
    for(global_links
    ,  
        if(__link_part_match(_, __link_part_new(underfoot, p~'dimension'))
        ,  __tp_to_link(p, _);
           return();
        );
    );
);


__tp_to_link(player, link) -> (
    if(pos_offset(pos(player), 'down', 1) == link:'first':'pos' //&& p~'dimension' == _:'first':'dim'
                ,   l = __link_part_new(pos_offset(link:'second':'pos', 'up', 1), link:'second':'dim');
                    __tp_player(player, l)
                ,   l = __link_part_new(pos_offset(link:'first':'pos', 'up', 1), link:'first':'dim');
                    __tp_player(player, l)
                );
);

__on_player_attacks_entity(player, entity) -> (  
  held_item = query(player, 'holds', 'mainhand');
  if(__holding_wand(player), 
    sound('entity.enderman.teleport', pos(player));
    last_position = global_player_coord;
    particle('sonic_boom', pos(entity));
    schedule(1, '__tp_player', entity, last_position);
    'cancel';
  );
);

__on_player_swings_hand(player, hand) -> (
  held_item = query(player, 'holds', 'mainhand');
  if(__holding_wand(player) && hand == 'mainhand' 
                            && query(player, 'xp_level') > 0 
                            && !query(player, 'trace', 4.5, 'blocks') 
                            && !query(player, 'trace', 4.5, 'entities'), 
                            
    __open_grid_of_player_heads(player, player('all'));
//    __tp_self_to_last_pos(player); 
    
  // , __holding_wand(player) && hand == 'mainhand' 
  //                          && query(player, 'xp_level') > 0 
  //                          && !query(player, 'trace', 4.5, 'blocks') 
  //                          && !query(player, 'trace', 4.5, 'entities') 
  //                          && __get_wearing_head(player), 
                           
  //   other_player = __get_other_player_wearing_my_head(player);
  //   __tp_to_other_player(other_player, player);
  );
  
);

__open_grid_of_player_heads(player, player_list) -> (
  
  //for(player_list, if(_ == player, delete(player_list, _i)));
  
  screen = create_screen(player,'generic_9x6', 'tpa', _(screen, player, action, data)->(
    
    if(action == 'pickup' && data:'slot' < 54 && inventory_get(screen, data:'slot') != null,
      player_head = inventory_get(screen, data:'slot'):2:'SkullOwner':'Name';
      other = __get_player_from_name(player_head);
      //__take_xp_level(player);
      __tp_player(player, __link_part_new(pos(other), other~'dimension'));
      close_screen(screen);
    );
    
    'cancel';
  ));
  
  task(_(outer(screen),outer(item_tuple),outer(player_list)) -> (
    
    if(screen_property(screen, 'open') == 'true',
     for(player_list,
       player_head = __get_player_head_tuple(_);
       inventory_set(screen, _i, 1, player_head:0, player_head:1);
      );
    );
  ));
);


__get_player_from_name(player) -> (
  for(player('all'), 
  if(_ == player,
    return(_) 
  )
  );
);

// __tp_to_other_player(other_player, player) -> (
//     if(__get_wearing_head(other_player) == player~'name'
//     , __tp_player(player, pos(other_player));
//       __take_xp_level(player);
//     , sound('block.note_block.cow_bell', pos(player));
//     );
// );
    
// __get_other_player_wearing_my_head(player) -> (
//   first(player('all'), _ == __get_wearing_head(player));
// );

__on_player_uses_item(player, item_tuple, hand) -> (
  looking_at = query(player, 'trace', 4.5, 'blocks');
  //link_part_looking_at = __link_part_new(pos(looking_at), player~'dimension');
  if(__holding_wand(player) && hand == 'mainhand' && looking_at == 'lodestone'
  , // global_second called first otherwise it's set immediately on first attempt 
    link_part_looking_at = __link_part_new(pos(looking_at), player~'dimension');
    if(global_part1:'pos' != null && global_part1:'dim' != null && !__link_part_global_links_match(link_part_looking_at)
    , part2 = __link_part_new(pos(looking_at), player~'dimension', player~'uuid');
      sound('block.end_portal_frame.fill', pos(player));
    );
    if(global_part1:'pos' == null && global_part1:'dim' == null && !__link_part_global_links_match(link_part_looking_at)
    , global_part1 = __link_part_new(pos(looking_at), player~'dimension', player~'uuid');
      sound('block.end_portal_frame.fill', pos(player));
    );
    
    if(part2:'pos' && part2:'dim' //&& !__link_part_global_links_match(link_part_looking_at)
    , sound('block.beacon.activate', pos(player));
      __link_store('__to_global_links', __link_new(global_part1, part2));
      __link_store('__to_disk', global_links);
      global_part1 = __link_part_new(null, null, null);
    );
    'cancel';
   
  //elseif
  ,  __holding_wand(player) && hand == 'mainhand'   
  ,  global_player_coord = __link_part_new(pos(player), player~'dimension');
    sound('entity.ender_eye.death', pos(player));
    'cancel';
  );
);

// __tp_self_to_last_pos(player) -> (
//     __take_xp_level(player);
//     sound('entity.enderman.teleport', pos(player));
//     last_position = global_player_coord;
//     particle('sonic_boom', pos(player));
//     global_player_coord = pos(player);
//     schedule(0, '__save_position', player);
//     schedule(1, '__tp_player', player, last_position);
// );


__holding_wand(player) -> (
  hand = query(player, 'holds', 'mainhand');
  if(hand:0 == 'stick' && __has_luck(hand:2)
  , true;
  , false;
  );
);

__has_luck(nbt) -> (
  // get enchantments list
  enchants_list = parse_nbt(nbt):'Enchantments';

  // check if enchants exist
  if(enchants_list != null,
    // find enchant that matches and check if not null
    first(enchants_list,
      // check if enchant is efficiency and is level 5 or more
      _:'id' == 'minecraft:luck' && _:'lvl' >= 1;
    ) != null,
  //else
    false
  );
);


// __get_wormhole_database() -> (
//   if(read_file('wormhole_links', 'json')
//   , global_wormhole_links = decode_json(read_file('wormhole_links', 'json'));
//   , global_wormhole_links = [];
//   );
// );

// __block_under_foot(pos) -> (
//   pos_offset(pos, 'down', 1);
// );

// __get_link_destination(pos) -> (
//   __find_wormhole_link(__new_link(__block_under_foot(pos), player()~'dimension', null, null));
// );

__on_player_breaks_block(player, block) -> (
  if(__link_part_global_links_match(__link_part_new(pos(block), player~'dimension', player~'uuid')) && block == 'lodestone' 
  , __link_delete_from_global_links(pos(block));
    __link_store('__to_disk', global_links);
    sound('block.beacon.deactivate', pos(player));
    print(__link_get_uuid(pos(block), player~'dimension', player~'uuid'));
  , __link_get_uuid(pos(block), player~'dimension', player~'uuid') != 0 
                    && __link_get_uuid(pos(block), player~'dimension', player~'uuid') != null 
                    && block == 'lodestone'
  ,  print(__link_get_uuid(pos(block), player~'dimension', player~'uuid'));
   'cancel';
  );
);

__link_delete_from_global_links(pos) -> (
    for(global_links,
      if(_:'first':'pos' == pos || _:'second':'pos' == pos
      , delete(global_links, _i); 
      );
    );
);


__get_wearing_head(player) -> (
  if(player
  , inventory_get(player, 39):2:'skullowner':'name';
  );
);

__take_xp_level(player) -> (
  xp = query(player, 'xp_level');
  modify(player, 'xp_level', xp-1);
);

__get_player_head_tuple(player) -> (
  ['player_head', '{SkullOwner:' + player + '}']
);

// __spawn_head(player, loc) -> (
//     xv = rand(0.5)-0.25;
//     yv = rand(0.5);
//     zv = rand(0.5)-0.25;
    
//     motion = '[' + xv + 'd, ' + yv + 'd, ' + zv + 'd' + ']';
//     data = '{motion: ' + motion + ', item: {id: "minecraft:player_head", count:1b, tag:{skullowner: "' + player + '"}}}, pickupdelay: 3s';
//     __take_xp_level(player);
//     sound('entity.generic.splash', pos(loc));
//     spawn('item', pos_offset(pos(loc), 'up', 1), data);
// ); 

__tp_player(player, link_part) -> (
    in_dimension(link_part:'dim', run('tp '+player~'uuid'+' '+link_part:'pos':0+' '+link_part:'pos':1+' '+link_part:'pos':2));
    sound('entity.enderman.teleport', pos(player));
    particle('sonic_boom', pos(player));
);

__save_position(player) -> (
    global_player_coord = pos(player);
);


////// LINK STUFF
__link_part_new(pos, dim, ... uuid) -> (
  if(!uuid, uuid = null);
    return(
        {
            'pos' -> pos
            ,'dim' -> dim
            ,'owner' -> uuid 
        };
    );
);

__link_match(self, other) -> (
    __link_part_match(other, self:'first')
    || __link_part_match(other, self:'second')
);

__link_part_match(link, part) -> (
    (part:'pos' == link:'first':'pos' && part:'dim' == link:'first':'dim' && part:'uuid' == link:'first':'uuid') 
    || (part:'pos' == link:'second':'pos' && part:'dim' == link:'second':'dim' && part:'uuid' == link:'second':'uuid')
);

__link_part_global_links_match(part) -> (
    first(global_links, __link_part_match(_, part))
);

__link_store(func, link) -> (
    call(func, link)
);

__link_new(part1, part2) -> (
    if(part1:'pos' < part2:'pos',
        return(
            {
               'first' -> part1
               ,'second' -> part2
            }
        );
    );
    return(
        {
           'first' -> part2
           ,'second' -> part1
        }
    );
);

__to_global_links(l) -> (
    global_links += l;
);

__to_disk(l) -> (
    write_file('links', 'json', l)
);

__link_sort(link_list) -> (
    sort_key(link_list, _:'link1':'pos')
);

__link_get_uuid(pos, dimension, uuid) -> (
  for(globel_links
  ,   if((_:'first':'pos' == pos && _:'first':'dim' == dimension) 
        || (_:'second':'pos' == pos && _:'second':'dim' == dimension)
      , print(_:'first':'owner') 
      ); 
  );
);