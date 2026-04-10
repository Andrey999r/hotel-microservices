package com.andrey999r.staynova.domain.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.user.BlankPicturePathException;
import com.andrey999r.staynova.domain.exceptions.user.NullPicturePathException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PicturePath")
class PicturePathTest {

  @Test
  @DisplayName("Создаёт PicturePath для корректного значения")
  void constructor_shouldCreatePicturePath_whenValueIsValid() {
    // arrange & act
    PicturePath path = new PicturePath("https://cdn.example.com/avatar.png");

    // assert
    assertThat(path.path()).isEqualTo("https://cdn.example.com/avatar.png");
  }

  @Test
  @DisplayName("Создаёт PicturePath для относительного пути (из БД)")
  void constructor_shouldCreatePicturePath_whenValueIsRelativePath() {
    // arrange & act
    PicturePath path = new PicturePath("default.png");

    // assert
    assertThat(path.path()).isEqualTo("default.png");
  }

  @Test
  @DisplayName("Бросает NullPicturePathException, если значение null")
  void constructor_shouldThrowNullPicturePathException_whenValueIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new PicturePath(null))
        .isInstanceOf(NullPicturePathException.class);
  }

  @Test
  @DisplayName("Бросает BlankPicturePathException, если значение пустое")
  void constructor_shouldThrowBlankPicturePathException_whenValueIsEmpty() {
    // arrange & act & assert
    assertThatThrownBy(() -> new PicturePath(""))
        .isInstanceOf(BlankPicturePathException.class);
  }

  @Test
  @DisplayName("Бросает BlankPicturePathException, если значение состоит из пробелов")
  void constructor_shouldThrowBlankPicturePathException_whenValueIsBlank() {
    // arrange & act & assert
    assertThatThrownBy(() -> new PicturePath("   "))
        .isInstanceOf(BlankPicturePathException.class);
  }
}
