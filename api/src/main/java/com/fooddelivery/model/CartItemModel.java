package com.fooddelivery.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CartItemModel
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-10-12T09:35:08.911055400+02:00[Europe/Warsaw]")
public class CartItemModel {

  private Long foodId;

  private Integer pieces;

  public CartItemModel() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CartItemModel(Long foodId, Integer pieces) {
    this.foodId = foodId;
    this.pieces = pieces;
  }

  public CartItemModel foodId(Long foodId) {
    this.foodId = foodId;
    return this;
  }

  /**
   * Get foodId
   * minimum: 1
   * @return foodId
  */
  @NotNull @Min(1L) 
  @Schema(name = "foodId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("foodId")
  public Long getFoodId() {
    return foodId;
  }

  public void setFoodId(Long foodId) {
    this.foodId = foodId;
  }

  public CartItemModel pieces(Integer pieces) {
    this.pieces = pieces;
    return this;
  }

  /**
   * Get pieces
   * minimum: 1
   * @return pieces
  */
  @NotNull @Min(1) 
  @Schema(name = "pieces", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pieces")
  public Integer getPieces() {
    return pieces;
  }

  public void setPieces(Integer pieces) {
    this.pieces = pieces;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CartItemModel cartItemModel = (CartItemModel) o;
    return Objects.equals(this.foodId, cartItemModel.foodId) &&
        Objects.equals(this.pieces, cartItemModel.pieces);
  }

  @Override
  public int hashCode() {
    return Objects.hash(foodId, pieces);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CartItemModel {\n");
    sb.append("    foodId: ").append(toIndentedString(foodId)).append("\n");
    sb.append("    pieces: ").append(toIndentedString(pieces)).append("\n");
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

