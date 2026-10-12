package com.fooddelivery.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fooddelivery.model.CartItemModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CartModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-11T21:27:24.655634300+02:00[Europe/Paris]")
public class CartModel {

  @Valid
  private List<@Valid CartItemModel> orderItemModels = new ArrayList<>();

  public CartModel() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CartModel(List<@Valid CartItemModel> orderItemModels) {
    this.orderItemModels = orderItemModels;
  }

  public CartModel orderItemModels(List<@Valid CartItemModel> orderItemModels) {
    this.orderItemModels = orderItemModels;
    return this;
  }

  public CartModel addOrderItemModelsItem(CartItemModel orderItemModelsItem) {
    if (this.orderItemModels == null) {
      this.orderItemModels = new ArrayList<>();
    }
    this.orderItemModels.add(orderItemModelsItem);
    return this;
  }

  /**
   * Get orderItemModels
   * @return orderItemModels
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "orderItemModels", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("orderItemModels")
  public List<@Valid CartItemModel> getOrderItemModels() {
    return orderItemModels;
  }

  public void setOrderItemModels(List<@Valid CartItemModel> orderItemModels) {
    this.orderItemModels = orderItemModels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CartModel cartModel = (CartModel) o;
    return Objects.equals(this.orderItemModels, cartModel.orderItemModels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderItemModels);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CartModel {\n");
    sb.append("    orderItemModels: ").append(toIndentedString(orderItemModels)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

