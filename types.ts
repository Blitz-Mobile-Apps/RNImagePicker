export interface ImagePickerOptions {
  selection: Selection,
  selectionLimit?: number;
  mediaType: MediaType;
  quality?: PhotoQuality;
  includeBase64?: boolean;
  selectMultiple?: boolean;

}

export type MediaType = 'photo';
export type Selection = 'camera' | 'gallery';
export type PhotoQuality =
  | 0.1
  | 0.2
  | 0.3
  | 0.4
  | 0.5
  | 0.6
  | 0.7
  | 0.8
  | 0.9
  | 1;
