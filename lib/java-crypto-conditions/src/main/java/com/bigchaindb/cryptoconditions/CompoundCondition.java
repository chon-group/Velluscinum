package com.bigchaindb.cryptoconditions;

import java.util.EnumSet;

public abstract interface CompoundCondition
  extends Condition
{
  public abstract EnumSet<ConditionType> getSubtypes();
}
