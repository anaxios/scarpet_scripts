//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_start() -> (
  __get_wormhole_database();
  global_first  = null;
  global_second = null;
);

__on_player_starts_sneaking(player) -> (
  if(__is_standing_on_wormhole(pos(player))
  , dest = pos_offset(__get_link_destination(pos(player)), 'up', 1);
    sound('entity.enderman.teleport', pos(player));
    schedule(0, '__tp_player', player, dest); 
  );
);

__on_player_attacks_entity(player, entity) -> (  
  held_item = query(player, 'holds', 'mainhand');
  if(__holding_wand(player) && query(player, 'xp_level') > 0
  , 
    xp = query(player, 'xp_level');
    modify(player, 'xp_level', xp-1);
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
                            && !query(player, 'trace', 4.5, 'entities') 
                            && !__get_wearing_head(player) 
  , 
    __take_xp_level(player);
    sound('entity.enderman.teleport', pos(player));
    last_position = global_player_coord;
    particle('sonic_boom', pos(player));
    global_player_coord = pos(player);
    schedule(0, '__save_position', player);
    schedule(1, '__tp_player', player, last_position);
  , __holding_wand(player) && hand == 'mainhand' 
                           && query(player, 'xp_level') > 0 
                           && !query(player, 'trace', 4.5, 'blocks') 
                           && !query(player, 'trace', 4.5, 'entities') 
                           && __get_wearing_head(player)
                           
  , other_player = first(player('all'), _ == __get_wearing_head(player));
    if(__get_wearing_head(other_player) == player~'name'
    , __tp_player(player, pos(other_player));
      __take_xp_level(player);
    , sound('block.note_block.cow_bell', pos(player));
    );
  );
  
);

__on_player_uses_item(player, item_tuple, hand) -> (
  looking_at = query(player, 'trace', 4.5, 'blocks');
  if(__holding_wand(player) && hand == 'mainhand' && query(player, 'xp_level') > 0 && looking_at == 'lodestone'
  , // global_second called first otherwise it's set immediately on first attempt 
    if(global_first != null && !__is_linked(looking_at)
    , global_second = looking_at;
      sound('block.end_portal_frame.fill', pos(player));
    );
    if(global_first == null && !__is_linked(looking_at)
    , global_first = looking_at;
      sound('block.end_portal_frame.fill', pos(player));
    );
    
    if(global_second && !__is_linked(looking_at)
    , sound('block.beacon.activate', pos(player));
      __set_wormhole_link(global_first, global_second);
      global_first  = null;
      global_second = null;
    );
    'cancel';
  
  //elseif
  , __holding_wand(player) && hand == 'mainhand' && query(player, 'xp_level') > 0 && looking_at == 'water_cauldron' && block_state(pos(looking_at)):'level' == 3
  , __spawn_head(player, looking_at);
    'cancel';
   
  //elseif
  ,  __holding_wand(player) && hand == 'mainhand' && query(player, 'xp_level') > 0   
  ,  global_player_coord = pos(player);
    sound('entity.ender_eye.death', pos(player));
    'cancel';
  );
);


__is_standing_on_wormhole(pos) -> (
  __is_linked(block(__block_under_foot(pos)));
);

__is_linked(block) -> (
  if(block == 'lodestone' && __find_wormhole_link(pos(block)) != 0 
  , true;
  ,false;
  );
); 

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


__get_wormhole_database() -> (
  if(read_file('wormhole_links', 'json')
  , global_wormhole_links = decode_json(read_file('wormhole_links', 'json'));
  , global_wormhole_links = [];
  );
);

__block_under_foot(pos) -> (
  pos_offset(pos, 'down', 1);
);

__get_link_destination(pos) -> (
  __find_wormhole_link(__block_under_foot(pos));
);

__on_player_breaks_block(player, block) -> (
  if(__is_linked(block) 
  , __delete_link_from_global_wormhole_links(pos(block));
    sound('block.beacon.deactivate', pos(player));
  );
);

__delete_link_from_global_wormhole_links(pos) -> (
    for(global_wormhole_links,
      if(_:0 == pos || _:1 == pos
      , delete(global_wormhole_links, _i); 
      );
    );
);

__set_wormhole_link(block1, block2) -> (
  if(!__is_linked(block1) && !__is_linked(block2)
  , global_wormhole_links += [pos(block1),pos(block2)];
    write_file('wormhole_links', 'json', encode_json(global_wormhole_links));
  );
);

__find_wormhole_link(pos) -> (
  for(global_wormhole_links
  , 
    if(_:0 == pos, return(_:1));
    if(_:1 == pos, return(_:0));
  );
);

__get_wearing_head(player) -> (
  if(player
  , inventory_get(player, 39):2:'SkullOwner':'Name';
  );
);

__take_xp_level(player) -> (
  xp = query(player, 'xp_level');
  modify(player, 'xp_level', xp-1);
);

__spawn_head(player, loc) -> (
    xv = rand(0.5)-0.25;
    yv = rand(0.5);
    zv = rand(0.5)-0.25;
    
    motion = '[' + xv + 'd, ' + yv + 'd, ' + zv + 'd' + ']';
    data = '{Motion: ' + motion + ', Item: {id: "minecraft:player_head", Count:1b, tag:{SkullOwner: "' + player + '"}}}, PickupDelay: 3s';
    __take_xp_level(player);
    sound('entity.generic.splash', pos(loc));
    spawn('item', pos_offset(pos(loc), 'up', 1), data);
); 

__tp_player(player, pos) -> (
    run('tp '+player~'uuid'+' '+pos:0+' '+pos:1+' '+pos:2);
    sound('entity.enderman.teleport', pos(player));
    particle('sonic_boom', pos(player));
);

__save_position(player) -> (
    global_player_coord = pos(player());
);

