
Pod::Spec.new do |s|
  s.name         = "rn-image-picker"
  s.version      = "2.0.5"
  s.homepage     = "https://salsoftmobile@bitbucket.org/salsoftmobileapphq/rn-image-picker.git"

  s.summary      = "rn-image-picker"
  s.description  = <<-DESC
  rn-image-picker
                   DESC
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "ammar.tariq@salsoft.net" }
  s.platform     = :ios, "12.0"
  s.source       = { :git => "https://salsoftmobile@bitbucket.org/salsoftmobileapphq/rn-image-picker.git", :tag => "master" }
  s.source_files  = "ios/**/*.{h,m}"
  s.requires_arc = true
  s.dependency "UIImage+ImageCompress"
  s.dependency "CLImageEditor/AllTools"
  s.dependency "React"
end

  