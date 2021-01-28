
Pod::Spec.new do |s|
  s.name         = "RNChatSocket"
  s.version      = "1.0.0"
  s.summary      = "RNChatSocket"
  s.description  = <<-DESC
                  RNChatSocket
                   DESC
  s.homepage     = "https://github.com/go-xe2/react-native-chatsocket.git"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "279197148@qq.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/go-xe2/react-native-chatsocket.git", :tag => "master" }
  s.source_files  = "RNChatsocket/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  #s.dependency "others"

end

