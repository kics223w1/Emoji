/*
 * Copyright (C) 2016 - Niklas Baudy, Ruben Gees, Mario Đanić and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.vanniktech.emoji

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.vanniktech.emoji.emoji.Emoji
import com.vanniktech.emoji.listeners.OnEmojiClickListener
import com.vanniktech.emoji.listeners.OnEmojiLongClickListener
import java.util.concurrent.Executors

internal class EmojiArrayAdapter(
  context: Context,
  emojis: Array<Emoji>,
  private val variantManager: VariantEmoji?,
  private val listener: OnEmojiClickListener?,
  private val longListener: OnEmojiLongClickListener?,
  private val theming: EmojiTheming,
) : ArrayAdapter<Emoji>(context, 0, Utils.asListWithoutDuplicates(emojis)) {
  private val executorService = Executors.newSingleThreadExecutor()

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var image = convertView as? EmojiImageView
    val context = context
    if (image == null) {
      image = LayoutInflater.from(context).inflate(R.layout.emoji_adapter_item_emoji, parent, false) as EmojiImageView
      image.setOnEmojiClickListener(listener)
      image.setOnEmojiLongClickListener(longListener)
    }
    val emoji = getItem(position)!!
    val variantToUse = variantManager?.getVariant(emoji) ?: emoji
    image.contentDescription = emoji.unicode
    image.setEmoji(executorService, theming, variantToUse)
    return image
  }

  fun updateEmojis(emojis: Collection<Emoji>) {
    clear()
    addAll(emojis)
    notifyDataSetChanged()
  }

  fun destroy() {
    executorService.shutdownNow()
  }
}