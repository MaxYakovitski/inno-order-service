package com.innowise.orderservice.service;

import com.innowise.orderservice.entity.Item;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ItemQueryService {

  Map<UUID, Item> findAllByIds(Collection<UUID> ids);
}
