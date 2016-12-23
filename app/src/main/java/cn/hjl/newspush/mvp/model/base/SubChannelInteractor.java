/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
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
package cn.hjl.newspush.mvp.model.base;


import cn.hjl.newspush.callback.RequestCallBack;
import rx.Subscription;

public interface SubChannelInteractor<T> {
    Subscription lodeNewsChannels(RequestCallBack<T> callback);
    Subscription lodeUnSubChannels(RequestCallBack<T> callBack);
}
